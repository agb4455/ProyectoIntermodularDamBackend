package com.tfm.db_back.api.dto;

import com.tfm.db_back.domain.model.ClanType;

import java.time.Instant;
import java.util.UUID;

public record CharacterResponseDto(
    UUID id,
    UUID userId,
    ClanType clanId,
    String name,
    Instant createdAt
) {}
