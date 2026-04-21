package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CharacterResponseDto;
import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.Character;
import com.tfm.db_back.domain.repository.CharacterRepository;
import com.tfm.db_back.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceImplTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CharacterServiceImpl characterService;

    private UUID userId;
    private UUID characterId;
    private Character character;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
        
        character = new Character(characterId, userId, "berserkers", "Ragnar", Instant.now());
    }

    @Test
    void createCharacter_Success() {
        // Arrange
        CreateCharacterRequestDto dto = new CreateCharacterRequestDto(userId, "berserkers", "Ragnar");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(characterRepository.save(any(Character.class))).thenReturn(character);

        // Act
        CharacterResponseDto response = characterService.createCharacter(dto);

        // Assert
        assertNotNull(response);
        assertEquals("Ragnar", response.name());
        assertEquals("berserkers", response.clanId());
        assertEquals(userId, response.userId());
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    void createCharacter_UserNotFound_ThrowsException() {
        // Arrange
        CreateCharacterRequestDto dto = new CreateCharacterRequestDto(userId, "berserkers", "Ragnar");
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> characterService.createCharacter(dto));
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void getCharacter_Success() {
        // Arrange
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // Act
        CharacterResponseDto response = characterService.getCharacter(characterId);

        // Assert
        assertNotNull(response);
        assertEquals(characterId, response.id());
    }

    @Test
    void getCharacter_NotFound_ThrowsException() {
        // Arrange
        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> characterService.getCharacter(characterId));
    }

    @Test
    void getCharactersByUser_Success() {
        // Arrange
        when(characterRepository.findByUserId(userId)).thenReturn(List.of(character));

        // Act
        List<CharacterResponseDto> list = characterService.getCharactersByUser(userId);

        // Assert
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(characterId, list.get(0).id());
    }
}
