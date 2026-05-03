package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad GameParticipant.
 * Proporciona acceso a la tabla "game_participants" de PostgreSQL.
 *
 * @author Adrián González Blando
 */
public interface GameParticipantRepository extends JpaRepository<GameParticipant, UUID> {

    /**
     * Recupera todos los participantes de una partida concreta.
     * Usado para construir el GameResponseDto con la lista de participantes.
     */
    List<GameParticipant> findByGameId(UUID gameId);
}
