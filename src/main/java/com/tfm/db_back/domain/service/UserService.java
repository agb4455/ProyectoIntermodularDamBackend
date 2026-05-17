package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.UserResponseDto;

import java.util.UUID;

/**
 * Contrato del servicio de usuarios.
 * Define las operaciones permitidas sobre la entidad User.
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
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
     * Verifica las credenciales de un usuario.
     * Usado por el Middle Server en el endpoint interno /internal/auth/verify.
     * Lanza EntityNotFoundException (404) si el usuario no existe.
     * Lanza UnauthorizedException (401) si la contraseña es incorrecta.
     */
    UserResponseDto verifyCredentials(String username, String password);

    /**
     * Actualiza la URL del avatar de un usuario existente.
     * Lanza EntityNotFoundException (404) si el usuario no existe.
     */
    void updateAvatar(UUID id, String avatarUrl);

    /**
     * Cambia la contraseña de un usuario verificando primero la actual (security.md §3).
     * Lanza EntityNotFoundException (404) si el usuario no existe.
     * Lanza UnauthorizedException (401) si la contraseña actual es incorrecta.
     */
    void changePassword(UUID id, String currentPassword, String newPassword);

    /**
     * Actualiza el email de un usuario existente.
     * Lanza EntityNotFoundException (404) si el usuario no existe.
     * Lanza ConflictException (409) si el email ya está en uso por otro usuario.
     */
    void updateEmail(UUID id, String newEmail);

    /**
     * Devuelve el número total de usuarios.
     */
    long getTotalUsers();

    /**
     * Devuelve el número de usuarios baneados.
     */
    long getBannedUsersCount();

    /**
     * Devuelve todos los usuarios del sistema.
     */
    java.util.List<UserResponseDto> getAllUsers();

    /**
     * Banea a un usuario por su ID.
     */
    void banUser(UUID id);

    /**
     * Desbanea a un usuario por su ID.
     */
    void unbanUser(UUID id);

    /**
     * Obtiene el ranking de los 3 mejores jugadores por Gloria Eterna (posiciones 1º, 2º y 3º).
     */
    java.util.List<com.tfm.db_back.api.dto.RankingUserResponseDto> getRanking();
}
