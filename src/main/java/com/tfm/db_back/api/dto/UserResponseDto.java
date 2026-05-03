package com.tfm.db_back.api.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para operaciones sobre usuarios.
 * NUNCA incluye el campo passwordHash — es el contrato de seguridad más importante de este dominio.
 * Se devuelve envuelto en ApiResponse<UserResponseDto>.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record UserResponseDto(

        UUID id,
        String username,
        String email,
        String avatarUrl,
        Instant createdAt,
        String role

) {
}
