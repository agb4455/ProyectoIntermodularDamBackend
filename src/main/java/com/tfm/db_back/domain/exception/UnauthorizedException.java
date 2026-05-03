package com.tfm.db_back.domain.exception;

/**
 * Excepción lanzada cuando las credenciales proporcionadas son inválidas.
 * Se mapea a un HTTP 401 Unauthorized en el GlobalExceptionHandler.
 *
 * @author Adrián González Blando
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
