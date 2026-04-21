package com.tfm.db_back.api.dto;

/**
 * Cuerpo de la respuesta exitosa del endpoint POST /internal/auth/handshake.
 * El token se devuelve envuelto en ApiResponse<HandshakeResponseDto>.
 */
public record HandshakeResponseDto(String token) {
}
