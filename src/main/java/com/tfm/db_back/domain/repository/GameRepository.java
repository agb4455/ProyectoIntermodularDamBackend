package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.Game;
import com.tfm.db_back.domain.model.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Game.
 * Proporciona acceso a la tabla "games" de PostgreSQL.
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
public interface GameRepository extends JpaRepository<Game, UUID> {

    /**
     * Recupera todas las partidas cuyo estado NO sea el indicado.
     * Usado en GET /internal/games/active → status != 'finished'.
     * Crítico para la recuperación del Middle tras un reinicio.
     */
    List<Game> findByStatusNot(GameStatus status);

    /**
     * Recupera todas las partidas en las que participa un usuario.
     * Realiza un JOIN entre games, game_participants y characters.
     */
    @org.springframework.data.jpa.repository.Query(
        "SELECT g FROM Game g " +
        "JOIN GameParticipant gp ON g.id = gp.gameId " +
        "JOIN Character c ON gp.characterId = c.id " +
        "WHERE c.userId = :userId"
    )
    List<Game> findByUserId(UUID userId);
}
