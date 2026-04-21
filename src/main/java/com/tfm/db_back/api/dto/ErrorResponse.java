package com.tfm.db_back.api.dto;

import java.time.Instant;

/**
 * Shape unificado de error para todas las respuestas de error de la API.
 * NUNCA incluye stack traces, rutas internas ni mensajes de JPA (security.md §8).
 */
public record ErrorResponse(String code, String message, Instant timestamp) {
}
