package com.tfm.db_back.api.dto;

import com.tfm.db_back.domain.model.ClanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO para la petición de creación de un nuevo personaje.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record CreateCharacterRequestDto(
    UUID userId,
    
    ClanType clanId,
    
    @NotBlank
    @Size(max = 100)
    String name
) {}
