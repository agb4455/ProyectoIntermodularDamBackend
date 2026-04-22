package com.tfm.db_back.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA que representa una partida en el sistema.
 * Mapea la tabla "games" definida en V1__initial_schema.sql.
 * El Middle Server es la fuente de verdad del estado en vivo —
 * esta entidad solo almacena metadatos de ciclo de vida.
 */
@Entity
@Table(name = "games")
public class Game {

    // UUID autogenerado por la base de datos — nunca auto-increment (security.md §1)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    // Estado de la partida: waiting | preparation | war | end | finished
    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    // Número máximo de jugadores permitidos (2-6 según arquitectura §7)
    @Column(name = "max_players", nullable = false)
    private short maxPlayers;

    // Fecha de creación del registro — se rellena en @PrePersist
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Fecha de inicio (cuando comienza la fase de preparación)
    @Column(name = "started_at")
    private Instant startedAt;

    // Fecha de finalización — nulo hasta que el Middle notifique el fin
    @Column(name = "ended_at")
    private Instant endedAt;

    // UUID del personaje ganador — nulo hasta que la partida termine
    @Column(name = "winner_character_id")
    private UUID winnerCharacterId;

    // Constructor vacío requerido por JPA
    public Game() {
    }

    // Constructor para crear una partida nueva (sin id ni timestamps — los gestiona la BD)
    public Game(GameStatus status, short maxPlayers) {
        this.status = status;
        this.maxPlayers = maxPlayers;
    }

    // Constructor completo para reconstrucción desde BD (tests y mapeos)
    public Game(UUID id, GameStatus status, short maxPlayers, Instant createdAt,
                Instant startedAt, Instant endedAt, UUID winnerCharacterId) {
        this.id = id;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.winnerCharacterId = winnerCharacterId;
    }

    // Inicializa la fecha de creación antes de la primera persistencia
    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- Getters y Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public short getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(short maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public UUID getWinnerCharacterId() {
        return winnerCharacterId;
    }

    public void setWinnerCharacterId(UUID winnerCharacterId) {
        this.winnerCharacterId = winnerCharacterId;
    }
}
