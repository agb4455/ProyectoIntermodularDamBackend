package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.api.dto.CharacterResponseDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.Character;
import com.tfm.db_back.domain.model.ClanType;
import com.tfm.db_back.domain.repository.CharacterRepository;
import com.tfm.db_back.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CharacterServiceImpl implements CharacterService {

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;

    public CharacterServiceImpl(CharacterRepository characterRepository, UserRepository userRepository) {
        this.characterRepository = characterRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CharacterResponseDto createCharacter(CreateCharacterRequestDto dto) {
        if (!userRepository.existsById(dto.userId())) {
            throw new EntityNotFoundException("User not found with id: " + dto.userId());
        }

        Character character = new Character(dto.userId(), dto.clanId(), dto.name());
        Character savedCharacter = characterRepository.save(character);
        
        return mapToResponseDto(savedCharacter);
    }

    @Override
    public CharacterResponseDto getCharacter(UUID id) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Character not found with id: " + id));
        return mapToResponseDto(character);
    }

    @Override
    public List<CharacterResponseDto> getCharactersByUser(UUID userId) {
        return characterRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private CharacterResponseDto mapToResponseDto(Character character) {
        return new CharacterResponseDto(
                character.getId(),
                character.getUserId(),
                character.getClanId(),
                character.getName(),
                character.getCreatedAt()
        );
    }
}
