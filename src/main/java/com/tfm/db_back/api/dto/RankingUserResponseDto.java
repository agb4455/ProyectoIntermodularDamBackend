package com.tfm.db_back.api.dto;

/**
 * DTO seguro para representar a un usuario en el ranking público.
 * Evita la exposición de datos sensibles como contraseña o email.
 */
public record RankingUserResponseDto(
    int position,
    String username,
    String avatarUrl,
    int gloriaEterna
) {}
