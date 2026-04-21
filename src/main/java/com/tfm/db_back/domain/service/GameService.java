package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateGameRequestDto;
import com.tfm.db_back.api.dto.EndGameRequestDto;
import com.tfm.db_back.api.dto.GameResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz del servicio de ciclo de vida de partidas.
 * dev_a implementa esta interfaz; dev_b la consume en GameController.
 * Acuerdo de Interface First — ambos desarrolladores deben acordar
 * las firmas antes de comenzar la implementación en paralelo.
 */
public interface GameService {

    /**
     * Crea un nuevo registro de partida con sus participantes iniciales.
     * El Middle llama a este endpoint al crear una partida en su memoria.
     */
    GameResponseDto createGame(CreateGameRequestDto dto);

    /**
     * Recupera una partida por su UUID.
     * Incluye el último volcado de estado disponible (state_json).
     * Lanza EntityNotFoundException si no existe.
     */
    GameResponseDto getGame(UUID id);

    /**
     * Lista todas las partidas con status != 'finished'.
     * Crítico para la recuperación del Middle tras un reinicio.
     * Debe ser una consulta eficiente (índice en games.status).
     */
    List<GameResponseDto> getActiveGames();

    /**
     * Marca una partida como finalizada.
     * - status → 'finished'
     * - winner_character_id → el del DTO (puede ser null en caso de empate)
     * - ended_at → Instant.now()
     */
    void endGame(UUID id, EndGameRequestDto dto);
}
