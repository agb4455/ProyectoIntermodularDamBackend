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
 * Entidad JPA que representa a un usuario del sistema.
 * Mapea la tabla "users" definida en V1__initial_schema.sql.
 * NUNCA se devuelve directamente desde el controlador — siempre se mapea a UserResponseDto.
 */
@Entity
@Table(name = "users")
public class User {

    // UUID autogenerado por la base de datos (gen_random_uuid()) — nunca auto-increment (security.md §1)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // Hash BCrypt del password — NUNCA se devuelve en ningún DTO de respuesta (security.md §3)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // URL del avatar almacenado en MinIO — nullable hasta que el usuario suba uno
    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    // Fecha de creación — se inicializa automáticamente antes de persistir
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Establece la fecha de creación justo antes de la primera persistencia.
     * Equivalente a @CreatedDate de Spring Data Auditing sin necesidad de configurarlo.
     */
    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public User() {
    }

    public User(UUID id, String username, String email, String passwordHash, String avatarUrl, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
