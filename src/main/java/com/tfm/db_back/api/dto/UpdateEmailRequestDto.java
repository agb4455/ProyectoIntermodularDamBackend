package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de petición para actualizar el email de un usuario.
 * Valida formato de email y longitud máxima (security.md §4).
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
public record UpdateEmailRequestDto(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email

) {
}
