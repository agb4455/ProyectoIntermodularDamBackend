package com.tfm.db_back.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AnalyticsSnapshotRequestDto(
        @NotBlank String gameId,
        @NotBlank String snapshotAt,
        @NotBlank String phase,
        @NotNull @NotEmpty List<PlayerSnapshotDto> players
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
}
