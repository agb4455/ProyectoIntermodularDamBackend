package com.tfm.db_back.api;

import com.tfm.db_back.domain.exception.ConflictException;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del GlobalExceptionHandler usando un controlador de prueba interno.
 */
class GlobalExceptionHandlerTest {

    // Controlador de prueba interno — lanza excepciones de forma controlada
    @RestController
    static class TestErrorController {

        // DTO mínimo para disparar validación
        record DummyDto(@NotBlank(message = "campo requerido") String campo) {}

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new EntityNotFoundException("Entidad de prueba no encontrada");
        }

        @GetMapping("/test/conflict")
        public void throwConflict() {
            throw new ConflictException("Recurso de prueba en conflicto");
        }

        @PostMapping("/test/validation")
        public void throwValidation(@Valid @RequestBody DummyDto dto) {
            // La excepción la lanza Spring automáticamente si la validación falla
        }

        @GetMapping("/test/error")
        public void throwUnexpected() {
            throw new RuntimeException("Error inesperado de prueba");
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestErrorController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void givenEntityNotFoundException_shouldReturn404WithCode_NOT_FOUND() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void givenConflictException_shouldReturn409WithCode_CONFLICT() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void givenMethodArgumentNotValid_shouldReturn400WithCode_VALIDATION_ERROR() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"campo\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void givenUnhandledException_shouldReturn500_andResponseBodyHasNoStackTrace() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                // Verificar que la respuesta NO contiene stack trace ni información interna
                .andExpect(jsonPath("$.message").value("Ha ocurrido un error interno. Contacta al administrador."))
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }
}
