package com.tfm.db_back.api.dto;

import java.util.UUID;

/**
 * DTO de entrada para finalizar una partida.
 * El Middle Server envía el UUID del personaje ganador al terminar la partida.
 * winnerCharacterId puede ser null en caso de empate o fin por abandono.
 */
public record EndGameRequestDto(
        // UUID del personaje ganador — puede ser null (empate o fin sin ganador claro)
        UUID winnerCharacterId
) {
}
