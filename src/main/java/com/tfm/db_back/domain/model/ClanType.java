package com.tfm.db_back.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de clanes disponibles en el juego.
 */
public enum ClanType {
    BERSERKERS,
    VALKIRIAS,
    JARLS,
    SKALDS,
    SEIDR,
    DRAUGR;

    @JsonCreator
    public static ClanType fromString(String value) {
        if (value == null) return null;
        return ClanType.valueOf(value.toUpperCase());
    }

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }
}
