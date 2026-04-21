package com.tfm.db_back.api.dto;

import java.time.Instant;
import java.util.UUID;

public record CharacterResponseDto(
    UUID id,
    UUID userId,
    String clanId,
    String name,
    Instant createdAt
) {}
