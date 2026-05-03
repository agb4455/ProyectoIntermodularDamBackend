package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;
import com.tfm.db_back.infrastructure.mongodb.BattleEventDocument;
import com.tfm.db_back.infrastructure.mongodb.BattleEventRepository;
import com.tfm.db_back.infrastructure.mongodb.GameSnapshotDocument;
import com.tfm.db_back.infrastructure.mongodb.GameSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de analíticas.
 * Procesa y persiste instantáneas y eventos de batalla en MongoDB de forma asíncrona.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private final GameSnapshotRepository gameSnapshotRepository;
    private final BattleEventRepository battleEventRepository;

    public AnalyticsServiceImpl(GameSnapshotRepository gameSnapshotRepository,
                                BattleEventRepository battleEventRepository) {
        this.gameSnapshotRepository = gameSnapshotRepository;
        this.battleEventRepository = battleEventRepository;
    }

    @Override
    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> saveSnapshot(AnalyticsSnapshotRequestDto dto) {
        try {
            GameSnapshotDocument document = mapToDocument(dto);
            gameSnapshotRepository.save(document);
            log.debug("Successfully saved analytics snapshot for game: {}", dto.gameId());

            if (dto.battleEvents() != null && !dto.battleEvents().isEmpty()) {
                List<BattleEventDocument> battleEvents = dto.battleEvents().stream()
                        .map(be -> mapToBattleEvent(dto.gameId(), be))
                        .collect(Collectors.toList());
                battleEventRepository.saveAll(battleEvents);
                log.debug("Successfully saved {} battle events for game: {}", battleEvents.size(), dto.gameId());
            }
        } catch (Exception e) {
            log.error("Failed to save analytics snapshot for game: {}. Error: {}", dto.gameId(), e.getMessage(), e);
            // We do not propagate the exception to keep it fire-and-forget
        }
        return CompletableFuture.completedFuture(null);
    }

    private GameSnapshotDocument mapToDocument(AnalyticsSnapshotRequestDto dto) {
        List<GameSnapshotDocument.PlayerSnapshot> playerSnapshots = dto.players().stream()
                .map(p -> {
                    List<GameSnapshotDocument.TroopSnapshot> troopSnapshots = p.troops() == null ? List.of() : p.troops().stream()
                            .map(t -> new GameSnapshotDocument.TroopSnapshot(
                                    t.troopId(),
                                    t.typeId(),
                                    t.currentPoints(),
                                    t.deployed()
                            )).collect(Collectors.toList());

                    return new GameSnapshotDocument.PlayerSnapshot(
                            p.characterId(),
                            p.clanId(),
                            p.economicCredits(),
                            p.researchCredits(),
                            p.capitalHealth(),
                            troopSnapshots,
                            p.unlockedResearches(),
                            p.eliminated()
                    );
                }).collect(Collectors.toList());

        return new GameSnapshotDocument(
                dto.gameId(),
                Instant.parse(dto.snapshotAt()),
                dto.phase(),
                playerSnapshots
        );
    }
    private BattleEventDocument mapToBattleEvent(String gameId, AnalyticsSnapshotRequestDto.BattleEventDto dto) {
        return new BattleEventDocument(
                gameId,
                Instant.parse(dto.timestamp()),
                dto.attackerCharacterId(),
                dto.attackerClanId(),
                dto.defenderCharacterId(),
                dto.defenderClanId(),
                dto.attackerTotalPoints(),
                dto.defenderTotalPoints(),
                dto.outcome(),
                dto.advantageApplied(),
                dto.advantageMultiplier(),
                dto.attackerTroopsLost(),
                dto.defenderTroopsLost()
        );
    }
}
