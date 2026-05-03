package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.UserResponseDto;
import com.tfm.db_back.domain.exception.ConflictException;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.User;
import com.tfm.db_back.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de usuarios.
 * Contiene toda la lógica de negocio del dominio User.
 * Los controladores NUNCA acceden directamente al repositorio.
 *
 * @author Adrián González Blando
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    // Bean PasswordEncoder definido en SecurityConfig — BCrypt con cost 12 (security.md §3)
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un usuario validando unicidad y hasheando la contraseña.
     * @throws ConflictException si el username o email ya están en uso (→ 409)
     */
    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto dto) {
        // Validar unicidad antes de persistir para evitar excepciones de constraint de BD
        if (userRepository.existsByUsername(dto.username())) {
            throw new ConflictException("El nombre de usuario '" + dto.username() + "' ya está en uso");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("El email '" + dto.email() + "' ya está registrado");
        }

        // Hashear contraseña con BCrypt antes de persistir — NUNCA se almacena en plano (security.md §3)
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    /**
     * Recupera un usuario por UUID.
     * @throws EntityNotFoundException si el UUID no existe en la BD (→ 404)
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        return toDto(user);
    }

    /**
     * Recupera un usuario por nombre de usuario — usado por el Middle Server en el login.
     * @throws EntityNotFoundException si el username no existe (→ 404)
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));
        return toDto(user);
    }

    /**
     * Verifica las credenciales de un usuario.
     * @throws com.tfm.db_back.domain.exception.UnauthorizedException si la contraseña es incorrecta (→ 401)
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto verifyCredentials(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("[Auth] Intento de login fallido para usuario: {} (Contraseña incorrecta)", username);
            throw new com.tfm.db_back.domain.exception.UnauthorizedException("Credenciales inválidas");
        }

        log.info("[Auth] Credenciales verificadas exitosamente para usuario: {}", username);
        return toDto(user);
    }

    /**
     * Actualiza la URL del avatar de un usuario existente.
     * @throws EntityNotFoundException si el UUID no existe (→ 404)
     */
    @Override
    @Transactional
    public void updateAvatar(UUID id, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        user.setAvatarUrl(avatarUrl);
        // JPA detecta el cambio automáticamente al finalizar la transacción (dirty checking)
    }

    /**
     * Mapea una entidad User a su DTO de respuesta.
     * GARANTIZA que passwordHash nunca aparece en la respuesta (security.md §3, §8).
     */
    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }
}
