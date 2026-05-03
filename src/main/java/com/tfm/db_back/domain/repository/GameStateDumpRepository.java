package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.GameStateDump;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad GameStateDump.
 * IMPORTANTE: Solo se realizan INSERTs. Nunca UPDATE ni DELETE.
 * El historial completo se conserva — el Middle siempre lee el más reciente.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public interface GameStateDumpRepository extends JpaRepository<GameStateDump, UUID> {

    /**
     * Recupera el volcado más reciente para una partida dada.
     * Equivale a: SELECT * FROM game_state_dumps WHERE game_id=? ORDER BY dumped_at DESC LIMIT 1
     * Usado en GET /internal/games/{id} para incluir el último estado conocido.
     */
    Optional<GameStateDump> findFirstByGameIdOrderByDumpedAtDesc(UUID gameId);
}
