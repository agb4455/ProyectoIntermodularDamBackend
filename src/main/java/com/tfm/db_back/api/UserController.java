package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.ApiResponse;
import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.UpdateAvatarRequestDto;
import com.tfm.db_back.api.dto.UserResponseDto;
import com.tfm.db_back.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para el dominio de usuarios.
 * Capa fina — delega toda la lógica a UserService.
 * Protegido por HandshakeJwtFilter: solo el Middle Server puede llamar estos endpoints.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@RestController
@RequestMapping("/internal/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /internal/users
     * Crea un nuevo usuario con su contraseña hasheada.
     * Devuelve 201 Created con el UserResponseDto (sin passwordHash).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestBody CreateUserRequestDto dto) {

        UserResponseDto created = userService.createUser(dto);
        return ResponseEntity.status(201).body(new ApiResponse<>(created));
    }

    /**
     * GET /internal/users/{id}
     * Recupera un usuario por UUID. Devuelve 404 si no existe.
     * UUID en path — nunca auto-increment (security.md §1).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable UUID id) {
        UserResponseDto user = userService.getUser(id);
        return ResponseEntity.ok(new ApiResponse<>(user));
    }

    /**
     * GET /internal/users/by-username/{username}
     * Recupera un usuario por nombre de usuario — usado por el Middle Server en el flujo de login.
     * Devuelve 404 si no existe.
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getByUsername(@PathVariable String username) {
        UserResponseDto user = userService.getByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(user));
    }

    /**
     * PUT /internal/users/{id}/avatar
     * Actualiza la URL del avatar tras la subida a MinIO desde el Middle Server.
     * Devuelve 204 No Content si la operación fue exitosa.
     */
    @PutMapping("/{id}/avatar")
    public ResponseEntity<Void> updateAvatar(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAvatarRequestDto dto) {

        userService.updateAvatar(id, dto.avatarUrl());
        return ResponseEntity.noContent().build();
    }
}
