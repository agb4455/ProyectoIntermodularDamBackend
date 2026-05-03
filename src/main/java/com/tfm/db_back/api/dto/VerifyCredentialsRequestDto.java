package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de petición para verificar credenciales de un usuario.
 * Usado por el Middle Server en el endpoint interno /internal/auth/verify.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record VerifyCredentialsRequestDto(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
