package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.UpdateAvatarRequestDto;
import com.tfm.db_back.api.dto.UserResponseDto;
import com.tfm.db_back.domain.exception.ConflictException;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.service.UserService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de capa web para UserController.
 * Usamos standaloneSetup para aislar el contenedor de Spring y testear exclusivamente
 * el enrutamiento HTTP y GlobalExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final UUID testId = UUID.randomUUID();
    private UserResponseDto testUserResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
                
        testUserResponse = new UserResponseDto(
                testId, "ragnar", "ragnar@lothbrok.com", "http://avatar.com/1", Instant.now(), "USER"
        );
    }

    @Test
    void createUser_givenValidDto_shouldReturn201() throws Exception {
        when(userService.createUser(any(CreateUserRequestDto.class))).thenReturn(testUserResponse);

        String payload = """
                {
                    "username": "ragnar",
                    "email": "ragnar@lothbrok.com",
                    "password": "strongPassword123"
                }
                """;

        mockMvc.perform(post("/internal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(testId.toString()))
                .andExpect(jsonPath("$.data.username").value("ragnar"))
                .andExpect(jsonPath("$.data.email").value("ragnar@lothbrok.com"));
                
        verify(userService).createUser(any(CreateUserRequestDto.class));
    }

    @Test
    void createUser_givenInvalidDto_shouldReturn400() throws Exception {
        String payload = """
                {
                    "username": "",
                    "email": "invalid-email",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/internal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
                
        verifyNoInteractions(userService);
    }

    @Test
    void createUser_givenDuplicateUsername_shouldReturn409() throws Exception {
        when(userService.createUser(any(CreateUserRequestDto.class)))
                .thenThrow(new ConflictException("Username ya existe"));

        String payload = """
                {
                    "username": "ragnar",
                    "email": "ragnar@lothbrok.com",
                    "password": "strongPassword123"
                }
                """;

        mockMvc.perform(post("/internal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void getUser_givenExistingId_shouldReturn200() throws Exception {
        when(userService.getUser(testId)).thenReturn(testUserResponse);

        mockMvc.perform(get("/internal/users/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testId.toString()))
                .andExpect(jsonPath("$.data.username").value("ragnar"));
    }

    @Test
    void getUser_givenNonExistingId_shouldReturn404() throws Exception {
        when(userService.getUser(testId)).thenThrow(new EntityNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/internal/users/{id}", testId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void getByUsername_givenExistingUsername_shouldReturn200() throws Exception {
        when(userService.getByUsername("ragnar")).thenReturn(testUserResponse);

        mockMvc.perform(get("/internal/users/by-username/{username}", "ragnar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("ragnar"));
    }

    @Test
    void getByUsername_givenNonExistingUsername_shouldReturn404() throws Exception {
        when(userService.getByUsername("ragnar"))
                .thenThrow(new EntityNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/internal/users/by-username/{username}", "ragnar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void updateAvatar_givenValidData_shouldReturn204() throws Exception {
        String payload = """
                {
                    "avatarUrl": "http://avatar.com/new"
                }
                """;

        mockMvc.perform(put("/internal/users/{id}/avatar", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNoContent());

        verify(userService).updateAvatar(testId, "http://avatar.com/new");
    }

    @Test
    void updateAvatar_givenNonExistingId_shouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Usuario no encontrado"))
                .when(userService).updateAvatar(eq(testId), anyString());

        String payload = """
                {
                    "avatarUrl": "http://avatar.com/new"
                }
                """;

        mockMvc.perform(put("/internal/users/{id}/avatar", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
