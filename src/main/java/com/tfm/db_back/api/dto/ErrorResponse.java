package com.tfm.db_back.api.dto;

import java.time.Instant;

/**
 * Shape unificado de error para todas las respuestas de error de la API.
 * NUNCA incluye stack traces, rutas internas ni mensajes de JPA (security.md §8).
 *
 * @author Adrián González Blando
 * @param code Código de error único (ej: "NOT_FOUND").
 * @param message Mensaje descriptivo amigable para el cliente.
 * @param timestamp Marca de tiempo en que ocurrió el error.
 */
public record ErrorResponse(String code, String message, Instant timestamp) {
}
