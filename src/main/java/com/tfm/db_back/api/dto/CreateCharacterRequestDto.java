package com.tfm.db_back.api.dto;

import com.tfm.db_back.domain.model.ClanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCharacterRequestDto(
    UUID userId,
    
    ClanType clanId,
    
    @NotBlank
    @Size(max = 100)
    String name
) {}
