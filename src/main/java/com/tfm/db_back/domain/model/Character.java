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

@Entity
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, name = "user_id")
    private UUID userId;

    @Column(nullable = false, name = "clan_id")
    private String clanId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    // Constructors (No Lombok)
    public Character() {
    }

    public Character(UUID userId, String clanId, String name) {
        this.userId = userId;
        this.clanId = clanId;
        this.name = name;
    }

    public Character(UUID id, UUID userId, String clanId, String name, Instant createdAt) {
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

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
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
