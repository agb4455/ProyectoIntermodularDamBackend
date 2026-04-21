package com.tfm.db_back.domain.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio que gestiona la generación y validación de JWT de handshake.
 * El token autentica al Middle Server como servicio — no a un usuario concreto.
 */
@Service
public class HandshakeService {

    private static final Logger log = LoggerFactory.getLogger(HandshakeService.class);

    private final SecretKey secretKey;
    private final long tokenTtlHours;

    public HandshakeService(
            @Value("${app.handshake-secret}") String secret,
            @Value("${app.handshake-token-ttl-hours:24}") long tokenTtlHours) {
        // La clave se deriva del secret de entorno — nunca hardcodeado (security.md §7)
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenTtlHours = tokenTtlHours;
    }

    /**
     * Genera un JWT firmado con HMAC-SHA para que el Middle Server se autentique.
     * Payload: iss, iat, exp — sin claims de usuario (es autenticación de servicio).
     */
    public String generateToken() {
        long nowMillis = System.currentTimeMillis();
        return Jwts.builder()
                .issuer("db-server")
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(nowMillis + tokenTtlHours * 3_600_000L))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Valida la firma y expiración de un token de handshake entrante.
     * NUNCA se loguea el token completo (security.md §11).
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Validación de token fallida: {}", ex.getMessage());
            return false;
        }
    }
}
