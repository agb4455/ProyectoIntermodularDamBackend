package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.CharacterResponseDto;
import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.domain.service.CharacterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CharacterResponseDto createCharacter(@Valid @RequestBody CreateCharacterRequestDto dto) {
        return characterService.createCharacter(dto);
    }

    @GetMapping("/{id}")
    public CharacterResponseDto getCharacter(@PathVariable UUID id) {
        return characterService.getCharacter(id);
    }

    @GetMapping("/by-user/{userId}")
    public List<CharacterResponseDto> getCharactersByUser(@PathVariable("userId") UUID userId) {
        return characterService.getCharactersByUser(userId);
    }
}
