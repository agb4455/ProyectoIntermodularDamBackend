package com.tfm.db_back.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfm.db_back.api.dto.CharacterResponseDto;
import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.service.CharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para CharacterController.
 * Utiliza standaloneSetup para máxima velocidad y evitar problemas de contexto en Java 25.
 */
@ExtendWith(MockitoExtension.class)
class CharacterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CharacterService characterService;

    @InjectMocks
    private CharacterController characterController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID userId;
    private UUID characterId;
    private CharacterResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(characterController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();
        responseDto = new CharacterResponseDto(characterId, userId, "berserkers", "Ragnar", Instant.now());
    }

    @Test
    void createCharacter_givenValidDto_shouldReturnCreated() throws Exception {
        CreateCharacterRequestDto dto = new CreateCharacterRequestDto(userId, "berserkers", "Ragnar");
        when(characterService.createCharacter(any(CreateCharacterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/internal/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(characterId.toString()))
                .andExpect(jsonPath("$.data.name").value("Ragnar"));
    }

    @Test
    void getCharacter_givenExistingId_shouldReturnOk() throws Exception {
        when(characterService.getCharacter(characterId)).thenReturn(responseDto);

        mockMvc.perform(get("/internal/characters/" + characterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(characterId.toString()))
                .andExpect(jsonPath("$.data.clanId").value("berserkers"));
    }

    @Test
    void getCharacter_givenNonExistingId_shouldReturnNotFound() throws Exception {
        when(characterService.getCharacter(characterId)).thenThrow(new EntityNotFoundException("No encontrado"));

        mockMvc.perform(get("/internal/characters/" + characterId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void getCharactersByUser_givenUserId_shouldReturnList() throws Exception {
        when(characterService.getCharactersByUser(userId)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/internal/characters/by-user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(characterId.toString()));
    }
}
