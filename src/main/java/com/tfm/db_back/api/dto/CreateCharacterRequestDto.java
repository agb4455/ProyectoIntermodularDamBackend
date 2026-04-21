package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCharacterRequestDto(
    UUID userId,
    
    @Pattern(regexp = "^(berserkers|valkirias|jarls|skalds|seidr|draugr)$", message = "Clan inválido")
    String clanId,
    
    @NotBlank
    @Size(max = 100)
    String name
) {}
