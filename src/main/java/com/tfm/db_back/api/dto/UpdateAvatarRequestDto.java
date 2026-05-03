package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de petición para actualizar la URL del avatar de un usuario.
 * La URL es generada por el Middle Server tras subir la imagen a MinIO.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record UpdateAvatarRequestDto(

        @NotBlank(message = "La URL del avatar es obligatoria")
        @Size(max = 512, message = "La URL del avatar no puede superar los 512 caracteres")
        String avatarUrl

) {
}
