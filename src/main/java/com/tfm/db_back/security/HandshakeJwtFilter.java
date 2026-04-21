package com.tfm.db_back.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfm.db_back.api.dto.ErrorResponse;
import com.tfm.db_back.domain.service.HandshakeService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Filtro JWT de handshake — intercepta todas las peticiones excepto POST /internal/auth/handshake.
 * Si el token es inválido o está ausente, devuelve 401 con ErrorResponse sin redirigir.
 */
@Component
public class HandshakeJwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HandshakeJwtFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HANDSHAKE_PATH = "/internal/auth/handshake";

    private final HandshakeService handshakeService;
    private final ObjectMapper objectMapper;

    public HandshakeJwtFilter(HandshakeService handshakeService, ObjectMapper objectMapper) {
        this.handshakeService = handshakeService;
        this.objectMapper = objectMapper;
    }

    /**
     * Excluye el endpoint de handshake del filtro — es el único punto de entrada sin token.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && HANDSHAKE_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Sin cabecera Authorization → 401 inmediato
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Petición sin token de handshake desde {} a {}", request.getRemoteAddr(), request.getRequestURI());
            writeUnauthorized(response, "MISSING_TOKEN", "Token de autenticación requerido");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Token presente pero inválido o expirado → 401
        if (!handshakeService.validateToken(token)) {
            log.warn("Token de handshake inválido o expirado desde {}", request.getRemoteAddr());
            // NUNCA se loguea el token completo (security.md §11)
            writeUnauthorized(response, "INVALID_TOKEN", "Token de autenticación inválido o expirado");
            return;
        }

        // Token válido — establecer autenticación en el contexto de seguridad de Spring
        var authentication = new UsernamePasswordAuthenticationToken(
                "middle-server",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_MIDDLE"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    /**
     * Escribe una respuesta 401 con el shape ErrorResponse estándar.
     * Se llama antes de continuar la cadena — corta el flujo inmediatamente.
     */
    private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ErrorResponse(code, message, Instant.now()));
    }
}
