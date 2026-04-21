package com.tfm.db_back.api;

import com.tfm.db_back.domain.service.HandshakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de capa web para AuthController.
 * Se usa standaloneSetup para inicializar MockMvc sin arrancar el contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HandshakeService handshakeService;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(handshakeService, "test-secret-mínimo-32-chars-ok!!");
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handshake_givenCorrectSecret_shouldReturn200AndToken() throws Exception {
        // given
        when(handshakeService.generateToken()).thenReturn("mocked-jwt-token");

        // when / then
        mockMvc.perform(post("/internal/auth/handshake")
                        .contentType(MediaType.APPLICATION_JSON)
                        // El secret de test coincide con app.handshake-secret del test properties
                        .content("{\"secret\": \"test-secret-mínimo-32-chars-ok!!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"));
    }

    @Test
    void handshake_givenWrongSecret_shouldReturn401WithCodeInvalidSecret() throws Exception {
        // when / then — secret diferente al configurado en test properties
        mockMvc.perform(post("/internal/auth/handshake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"secret\": \"secret-incorrecto\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_SECRET"));
    }

    @Test
    void handshake_givenBlankSecret_shouldReturn400ValidationError() throws Exception {
        // when / then
        mockMvc.perform(post("/internal/auth/handshake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"secret\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void handshake_givenNoBody_shouldReturn400() throws Exception {
        // when / then
        mockMvc.perform(post("/internal/auth/handshake")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
