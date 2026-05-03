package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de la petición al endpoint POST /internal/auth/handshake.
 * El campo secret NUNCA se loguea (security.md §11).
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record HandshakeRequestDto(@NotBlank(message = "El secret no puede estar vacío") String secret) {
}
