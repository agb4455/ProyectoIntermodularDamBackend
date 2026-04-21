package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.UserResponseDto;
import com.tfm.db_back.domain.exception.ConflictException;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.User;
import com.tfm.db_back.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios de UserServiceImpl.
 * Sin contexto Spring — instanciación con Mockito para máxima velocidad.
 * Nomenclatura: methodName_givenContext_shouldExpectedBehavior
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "thor";
    private static final String EMAIL = "thor@asgard.com";
    private static final String PASSWORD = "midgard123";
    private static final String HASHED_PASSWORD = "$2a$12$hashedpassword";
    private static final String AVATAR_URL = "https://minio.example.com/avatars/thor.webp";

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User(
                USER_ID,
                USERNAME,
                EMAIL,
                HASHED_PASSWORD,
                AVATAR_URL,
                Instant.now()
        );
    }

    // --- createUser ---

    @Test
    void createUser_givenValidDto_shouldHashPasswordAndReturnDtoWithoutHash() {
        // given
        var dto = new CreateUserRequestDto(USERNAME, EMAIL, PASSWORD);
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // when
        UserResponseDto result = userService.createUser(dto);

        // then
        assertThat(result.id()).isEqualTo(USER_ID);
        assertThat(result.username()).isEqualTo(USERNAME);
        assertThat(result.email()).isEqualTo(EMAIL);
        // Garantizar que el hash nunca aparece en ningún campo del DTO (security.md §3)
        assertThat(result.toString()).doesNotContain(HASHED_PASSWORD);
        verify(passwordEncoder).encode(PASSWORD);
    }

    @Test
    void createUser_givenDuplicateUsername_shouldThrowConflictException() {
        // given
        var dto = new CreateUserRequestDto(USERNAME, EMAIL, PASSWORD);
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(USERNAME);

        // Verificar que no se intentó guardar nada
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_givenDuplicateEmail_shouldThrowConflictException() {
        // given
        var dto = new CreateUserRequestDto(USERNAME, EMAIL, PASSWORD);
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(EMAIL);

        verify(userRepository, never()).save(any());
    }

    // --- getUser ---

    @Test
    void getUser_givenValidId_shouldReturnUserResponseDto() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));

        // when
        UserResponseDto result = userService.getUser(USER_ID);

        // then
        assertThat(result.id()).isEqualTo(USER_ID);
        assertThat(result.username()).isEqualTo(USERNAME);
    }

    @Test
    void getUser_givenNonExistentId_shouldThrowEntityNotFoundException() {
        // given
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.getUser(unknownId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    // --- getByUsername ---

    @Test
    void getByUsername_givenExistingUsername_shouldReturnUserResponseDto() {
        // given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existingUser));

        // when
        UserResponseDto result = userService.getByUsername(USERNAME);

        // then
        assertThat(result.username()).isEqualTo(USERNAME);
    }

    @Test
    void getByUsername_givenNonExistentUsername_shouldThrowEntityNotFoundException() {
        // given
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.getByUsername("unknown"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // --- updateAvatar ---

    @Test
    void updateAvatar_givenValidIdAndUrl_shouldPersistNewAvatarUrl() {
        // given
        String newUrl = "https://minio.example.com/avatars/new.webp";
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));

        // when
        userService.updateAvatar(USER_ID, newUrl);

        // then — JPA dirty checking actualiza al finalizar la transacción
        assertThat(existingUser.getAvatarUrl()).isEqualTo(newUrl);
    }

    @Test
    void updateAvatar_givenNonExistentId_shouldThrowEntityNotFoundException() {
        // given
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.updateAvatar(unknownId, AVATAR_URL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    // --- toDto (verificación de seguridad) ---

    @Test
    void getUser_givenValidUser_shouldNeverExposePasswordHash() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));

        // when
        UserResponseDto result = userService.getUser(USER_ID);

        // then — ningún campo del DTO debe contener el hash (security.md §3)
        assertThat(result.toString()).doesNotContain(HASHED_PASSWORD);
    }
}
