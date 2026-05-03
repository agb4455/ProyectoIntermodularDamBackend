package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO para la recepción de instantáneas de analítica (MongoDB).
 * Contiene el estado completo de una partida en un momento dado para su posterior análisis.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public record AnalyticsSnapshotRequestDto(
        @NotBlank String gameId,
        @NotBlank String snapshotAt,
        @NotBlank String phase,
        @NotNull @NotEmpty List<PlayerSnapshotDto> players,
        List<BattleEventDto> battleEvents
) {
    public record PlayerSnapshotDto(
            String characterId,
            String clanId,
            int economicCredits,
            int researchCredits,
            int capitalHealth,
            List<TroopSnapshotDto> troops,
            List<String> unlockedResearches,
            boolean eliminated
    ) {}

    public record TroopSnapshotDto(
            String troopId,
            String typeId,
            int currentPoints,
            boolean deployed
    ) {}

    public record BattleEventDto(
            String timestamp,
            String attackerCharacterId,
            String attackerClanId,
            String defenderCharacterId,
            String defenderClanId,
            int attackerTotalPoints,
            int defenderTotalPoints,
            String outcome,
            boolean advantageApplied,
            double advantageMultiplier,
            List<String> attackerTroopsLost,
            List<String> defenderTroopsLost
    ) {}
}
