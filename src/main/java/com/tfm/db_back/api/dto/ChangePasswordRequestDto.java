package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de petición para cambiar la contraseña de un usuario.
 * Requiere la contraseña actual para verificación (security.md §3).
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
public record ChangePasswordRequestDto(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
        String newPassword

) {
}
