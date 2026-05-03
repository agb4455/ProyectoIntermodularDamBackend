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
 * Entidad JPA que representa a un personaje (Character) en el sistema.
 * Un personaje pertenece a un usuario y a un clan específico.
 *
 * @author Adrián González Blando
 */
@Entity
@Table(name = "characters")
public class Character {

    /**
     * Identificador único del personaje (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * Identificador del usuario propietario del personaje.
     */
    @Column(nullable = false, name = "user_id")
    private UUID userId;

    /**
     * Tipo de clan al que pertenece el personaje.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "clan_id")
    private ClanType clanId;

    /**
     * Nombre del personaje.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Fecha y hora de creación del personaje.
     */
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    // Constructors (No Lombok)
    public Character() {
    }

    public Character(UUID userId, ClanType clanId, String name) {
        this.userId = userId;
        this.clanId = clanId;
        this.name = name;
    }

    public Character(UUID id, UUID userId, ClanType clanId, String name, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.clanId = clanId;
        this.name = name;
        this.createdAt = createdAt;
    }

    // Lifecycle hooks
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ClanType getClanId() {
        return clanId;
    }

    public void setClanId(ClanType clanId) {
        this.clanId = clanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
