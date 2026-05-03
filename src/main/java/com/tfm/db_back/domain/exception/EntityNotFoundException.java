package com.tfm.db_back.domain.exception;

/**
 * Excepción lanzada cuando una entidad buscada no existe en la base de datos.
 * El GlobalExceptionHandler la mapea a HTTP 404.
 *
 * @author Adrián González Blando
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
