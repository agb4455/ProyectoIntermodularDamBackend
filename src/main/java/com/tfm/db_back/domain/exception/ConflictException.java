package com.tfm.db_back.domain.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe (duplicado).
 * El GlobalExceptionHandler la mapea a HTTP 409.
 *
 * @author Adrián González Blando
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
