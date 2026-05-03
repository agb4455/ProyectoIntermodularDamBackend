package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de petición para crear un nuevo usuario.
 * Validado con Bean Validation antes de llegar al servicio (security.md §4).
 * NUNCA se devuelve este DTO en la respuesta — se mapea a UserResponseDto.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record CreateUserRequestDto(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password

) {
}
