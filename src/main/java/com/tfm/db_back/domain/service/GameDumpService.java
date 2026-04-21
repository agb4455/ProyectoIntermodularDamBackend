package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.StateDumpRequestDto;

import java.util.UUID;

/**
 * Interfaz del servicio de volcados de estado de partida.
 * dev_b implementa esta interfaz y la expone a través de GameController.
 * Solo realiza INSERTs en game_state_dumps — nunca UPDATE ni DELETE.
 */
public interface GameDumpService {

    /**
     * Inserta un nuevo volcado de estado en game_state_dumps.
     * El stateJson es un String opaco — nunca se deserializa en el DB Server.
     * Lanza EntityNotFoundException si el gameId no existe en la tabla games.
     */
    void dumpState(UUID gameId, StateDumpRequestDto dto);

    /**
     * Recupera el string JSON del volcado más reciente para una partida.
     * Devuelve null si no existe ningún volcado todavía (partida recién creada).
     */
    String getLatestDump(UUID gameId);
}
