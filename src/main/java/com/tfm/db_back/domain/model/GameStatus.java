package com.tfm.db_back.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estados posibles de una partida.
 * WAITING: Esperando jugadores.
 * PREPARATION: Fase de preparación (5 min).
 * WAR: Fase de guerra (recursos y ataques).
 * END: Fase final (2 jugadores).
 * FINISHED: Partida terminada.
 *
 * @author Adrián González Blando
 */
public enum GameStatus {
    WAITING,
    PREPARATION,
    WAR,
    END,
    FINISHED;

    /**
     * Convierte un string al enum ignorando mayúsculas/minúsculas.
     */
    @JsonCreator
    public static GameStatus fromString(String value) {
        if (value == null) return null;
        return GameStatus.valueOf(value.toUpperCase());
    }

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }
}
