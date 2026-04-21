package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Game.
 * Proporciona acceso a la tabla "games" de PostgreSQL.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {

    /**
     * Recupera todas las partidas cuyo estado NO sea el indicado.
     * Usado en GET /internal/games/active → status != 'finished'.
     * Crítico para la recuperación del Middle tras un reinicio.
     */
    List<Game> findByStatusNot(String status);
}
