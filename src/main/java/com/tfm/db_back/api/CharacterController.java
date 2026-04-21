package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.ApiResponse;
import com.tfm.db_back.api.dto.CharacterResponseDto;
import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.domain.service.CharacterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CharacterResponseDto>> createCharacter(@Valid @RequestBody CreateCharacterRequestDto dto) {
        CharacterResponseDto created = characterService.createCharacter(dto);
        return ResponseEntity.status(201).body(new ApiResponse<>(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CharacterResponseDto>> getCharacter(@PathVariable UUID id) {
        CharacterResponseDto character = characterService.getCharacter(id);
        return ResponseEntity.ok(new ApiResponse<>(character));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<List<CharacterResponseDto>>> getCharactersByUser(@PathVariable("userId") UUID userId) {
        List<CharacterResponseDto> list = characterService.getCharactersByUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(list));
    }
}
