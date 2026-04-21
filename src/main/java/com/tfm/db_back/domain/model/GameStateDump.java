package com.tfm.db_back.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA que representa un volcado periódico del estado de una partida.
 * Mapea la tabla "game_state_dumps" definida en V1__initial_schema.sql.
 *
 * REGLA CRÍTICA: Esta entidad es solo de escritura (INSERT). Nunca se actualiza ni borra.
 * El Middle Server siempre recupera el volcado más reciente via ORDER BY dumped_at DESC LIMIT 1.
 *
 * El campo state_json es un String opaco — el DB Server nunca lo deserializa.
 * PostgreSQL lo almacena como columna JSONB para optimizar consultas futuras.
 */
@Entity
@Table(name = "game_state_dumps")
public class GameStateDump {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    // FK a la partida cuyo estado se está volcando
    @Column(name = "game_id", nullable = false, updatable = false)
    private UUID gameId;

    // JSON serializado del GameState del Middle — almacenado como JSONB en PostgreSQL.
    // El DB Server lo trata como String opaco: nunca lo parsea ni modifica.
    @Column(name = "state_json", nullable = false, columnDefinition = "jsonb")
    private String stateJson;

    // Timestamp del volcado — se inicializa en @PrePersist
    @Column(name = "dumped_at", nullable = false, updatable = false)
    private Instant dumpedAt;

    // Constructor vacío requerido por JPA
    public GameStateDump() {
    }

    // Constructor para crear un nuevo volcado
    public GameStateDump(UUID gameId, String stateJson) {
        this.gameId = gameId;
        this.stateJson = stateJson;
    }

    // Constructor completo para reconstrucción desde BD (tests)
    public GameStateDump(UUID id, UUID gameId, String stateJson, Instant dumpedAt) {
        this.id = id;
        this.gameId = gameId;
        this.stateJson = stateJson;
        this.dumpedAt = dumpedAt;
    }

    // Inicializa el timestamp antes de la primera y única persistencia
    @PrePersist
    void onDump() {
        this.dumpedAt = Instant.now();
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

    public String getStateJson() {
        return stateJson;
    }

    public void setStateJson(String stateJson) {
        this.stateJson = stateJson;
    }

    public Instant getDumpedAt() {
        return dumpedAt;
    }

    public void setDumpedAt(Instant dumpedAt) {
        this.dumpedAt = dumpedAt;
    }
}
