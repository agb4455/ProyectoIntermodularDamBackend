package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.api.dto.CharacterResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * Contrato del servicio de personajes (Characters).
 * Define las operaciones para la gestión de personajes de los usuarios.
 *
 * @author Adrián González Blando
 */
public interface CharacterService {
    
    CharacterResponseDto createCharacter(CreateCharacterRequestDto dto);
    
    CharacterResponseDto getCharacter(UUID id);
    
    List<CharacterResponseDto> getCharactersByUser(UUID userId);
}
