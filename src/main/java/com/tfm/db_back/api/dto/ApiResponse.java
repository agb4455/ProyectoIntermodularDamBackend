package com.tfm.db_back.api.dto;

/**
 * Wrapper de éxito para todas las respuestas de la API.
 * Garantiza que los clientes siempre reciban un campo "data" consistente.
 *
 * @author Adrián González Blando
 * @param data Contenido de la respuesta.
 */
public record ApiResponse<T>(T data) {
}
