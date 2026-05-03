package com.tfm.db_back.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA que representa un participante en una partida.
 * Mapea la tabla "game_participants" definida en V1__initial_schema.sql.
 * Relación: una partida tiene entre 2 y 6 participantes.
 *
 * @author Adrián González Blando
 */
@Entity
@Table(name = "game_participants")
public class GameParticipant {

    /**
     * Identificador único del participante (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * Identificador de la partida a la que pertenece el participante.
     */
    @Column(name = "game_id", nullable = false, updatable = false)
    private UUID gameId;

    /**
     * Identificador del personaje que participa en la partida.
     */
    @Column(name = "character_id", nullable = false, updatable = false)
    private UUID characterId;

    /**
     * Orden de entrada en la partida (usado para posicionamiento).
     */
    @Column(name = "join_order", nullable = false)
    private short joinOrder;

    /**
     * Indica si el jugador ha sido eliminado de la partida.
     */
    @Column(nullable = false)
    private boolean eliminated;

    // Constructor vacío requerido por JPA
    public GameParticipant() {
    }

    // Constructor para registrar un nuevo participante
    public GameParticipant(UUID gameId, UUID characterId, short joinOrder) {
        this.gameId = gameId;
        this.characterId = characterId;
        this.joinOrder = joinOrder;
        this.eliminated = false;
    }

    // Constructor completo para reconstrucción desde BD
    public GameParticipant(UUID id, UUID gameId, UUID characterId, short joinOrder, boolean eliminated) {
        this.id = id;
        this.gameId = gameId;
        this.characterId = characterId;
        this.joinOrder = joinOrder;
        this.eliminated = eliminated;
    }

    // --- Getters y Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public void setCharacterId(UUID characterId) {
        this.characterId = characterId;
    }

    public short getJoinOrder() {
        return joinOrder;
    }

    public void setJoinOrder(short joinOrder) {
        this.joinOrder = joinOrder;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}
