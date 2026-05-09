package com.tfm.db_back.api.dto;

/**
 * DTO para las estadísticas individuales de un usuario.
 */
public record UserStatsResponseDto(
    long totalWins,
    long totalAttacks,
    long totalTroopsLost,
    long totalTrained,
    long totalCreditsEarned,
    long totalPlayTimeMinutes
) {}
