package com.tfm.db_back.api.dto;

import com.tfm.db_back.domain.model.ClanType;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta para representar un personaje.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record CharacterResponseDto(
    UUID id,
    UUID userId,
    ClanType clanId,
    String name,
    Instant createdAt
) {}
