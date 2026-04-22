package com.tfm.db_back.config;

import com.tfm.db_back.domain.service.HandshakeService;
import com.tfm.db_back.security.HandshakeJwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security para el DB Server.
 * API interna — CSRF desactivado, sesiones stateless, todo requiere JWT de handshake.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HandshakeJwtFilter handshakeJwtFilter;

    public SecurityConfig(HandshakeService handshakeService, ObjectMapper objectMapper) {
        this.handshakeJwtFilter = new HandshakeJwtFilter(handshakeService, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF no es necesario — API interna autenticada por JWT de servicio
                .csrf(AbstractHttpConfigurer::disable)
                // Sin sesiones HTTP — cada petición se autentica con el token
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Único endpoint público — el Middle Server obtiene aquí su JWT
                        .requestMatchers(HttpMethod.POST, "/internal/auth/handshake").permitAll()
                        // Todo lo demás requiere un token de handshake válido
                        .anyRequest().authenticated()
                )
                // Registrar el filtro JWT antes del filtro de credenciales estándar de Spring
                .addFilterBefore(handshakeJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h
                        .contentTypeOptions(c -> { /* habilita X-Content-Type-Options: nosniff */ })
                        .frameOptions(fo -> fo.deny())
                        .referrerPolicy(r -> r.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )
                .build();
    }

    /**
     * Bean de codificación de contraseñas con BCrypt.
     * Cost factor 12 — cumple security.md §3 (cost >= 12).
     * Inyectado en UserServiceImpl para hashear passwords antes de persistir.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
