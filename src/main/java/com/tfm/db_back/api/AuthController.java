package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.ApiResponse;
import com.tfm.db_back.api.dto.ErrorResponse;
import com.tfm.db_back.api.dto.HandshakeRequestDto;
import com.tfm.db_back.api.dto.HandshakeResponseDto;
import com.tfm.db_back.domain.service.HandshakeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

/**
 * Controlador de autenticación interna.
 * Único endpoint público de la API — el Middle Server obtiene aquí su JWT de handshake.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@RestController
@RequestMapping("/internal/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final HandshakeService handshakeService;
    // Pre-computamos los bytes del secret para la comparación en tiempo constante
    private final byte[] expectedSecretBytes;

    public AuthController(
            HandshakeService handshakeService,
            @Value("${app.handshake-secret}") String handshakeSecret) {
        this.handshakeService = handshakeService;
        this.expectedSecretBytes = handshakeSecret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * POST /internal/auth/handshake
     * El Middle Server envía el secret compartido y recibe un JWT de autenticación.
     * Comparación en tiempo constante para evitar timing attacks (security.md §3 / sprint1_detail).
     */
    @PostMapping("/handshake")
    public ResponseEntity<?> handshake(
            @Valid @RequestBody HandshakeRequestDto request,
            HttpServletRequest httpRequest) {

        byte[] providedBytes = request.secret().getBytes(StandardCharsets.UTF_8);

        // MessageDigest.isEqual realiza comparación en tiempo constante (previene timing attacks)
        if (!MessageDigest.isEqual(expectedSecretBytes, providedBytes)) {
            // Loguear IP y timestamp — NUNCA el secret enviado (security.md §11)
            log.warn("Intento de handshake fallido desde {} a las {}", httpRequest.getRemoteAddr(), Instant.now());
            return ResponseEntity
                    .status(401)
                    .body(new ErrorResponse("INVALID_SECRET", "Secret de handshake inválido", Instant.now()));
        }

        String token = handshakeService.generateToken();
        return ResponseEntity.ok(new ApiResponse<>(new HandshakeResponseDto(token)));
    }
}
