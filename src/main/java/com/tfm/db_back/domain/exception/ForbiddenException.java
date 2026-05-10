package com.tfm.db_back.domain.exception;

/**
 * Excepción lanzada cuando el usuario tiene prohibido realizar una acción
 * (ej. está baneado). Se mapea a un HTTP 403 Forbidden.
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
