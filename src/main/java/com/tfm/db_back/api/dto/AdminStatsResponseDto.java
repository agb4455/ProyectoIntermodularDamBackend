package com.tfm.db_back.api.dto;

/**
 * DTO para las estadísticas globales del administrador.
 */
public record AdminStatsResponseDto(
    long totalUsers,
    long totalGames,
    long bannedUsers
) {}
