package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.UserResponseDto;

import java.util.UUID;

/**
 * Contrato del servicio de usuarios.
 * Acordado entre dev_a (implementación) y dev_b (controlador) antes de programar en paralelo.
 * dev_b puede usar @MockBean de esta interfaz en sus tests de controlador.
 */
public interface UserService {

    /**
     * Crea un nuevo usuario hasheando su contraseña con BCrypt.
     * Lanza ConflictException (409) si el username o email ya existen.
     */
    UserResponseDto createUser(CreateUserRequestDto dto);

    /**
     * Recupera un usuario por su UUID.
     * Lanza EntityNotFoundException (404) si no existe.
     */
    UserResponseDto getUser(UUID id);

    /**
     * Recupera un usuario por su nombre de usuario.
     * Usado por el Middle Server para validar el login.
     * Lanza EntityNotFoundException (404) si no existe.
     */
    UserResponseDto getByUsername(String username);

    /**
     * Actualiza la URL del avatar de un usuario existente.
     * Lanza EntityNotFoundException (404) si el usuario no existe.
     */
    void updateAvatar(UUID id, String avatarUrl);
}
