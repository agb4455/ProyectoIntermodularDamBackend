package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el volcado periódico del estado de una partida.
 * El Middle envía el GameState serializado como String JSON opaco cada ~15 minutos.
 * El DB Server lo persiste tal cual en la columna JSONB sin procesarlo.
 */
public record StateDumpRequestDto(

        // Estado completo de la partida serializado como JSON por el Middle Server.
        // El DB Server NUNCA deserializa este campo — lo trata como String opaco.
        @NotBlank(message = "stateJson no puede estar vacío")
        String stateJson
) {
}
