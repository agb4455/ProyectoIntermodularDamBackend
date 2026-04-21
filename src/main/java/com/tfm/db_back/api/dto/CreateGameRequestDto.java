package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * DTO de entrada para crear una nueva partida.
 * El Middle Server envía este payload al crear la partida en su memoria.
 */
public record CreateGameRequestDto(

        // Número máximo de jugadores (2-6 según arquitectura §7)
        @NotNull(message = "maxPlayers es obligatorio")
        @Min(value = 2, message = "Una partida necesita mínimo 2 jugadores")
        @Max(value = 6, message = "Una partida puede tener máximo 6 jugadores")
        Short maxPlayers,

        // Lista de UUIDs de personajes que participan en la partida
        @NotEmpty(message = "La lista de characterIds no puede estar vacía")
        @Size(min = 2, max = 6, message = "Debe haber entre 2 y 6 participantes")
        List<UUID> characterIds
) {
}
