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
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private final GameSnapshotRepository gameSnapshotRepository;
    private final BattleEventRepository battleEventRepository;
    private final com.tfm.db_back.domain.repository.CharacterRepository characterRepository;
    private final com.tfm.db_back.domain.repository.GameRepository gameRepository;
    private final com.tfm.db_back.domain.repository.UserRepository userRepository;

    public AnalyticsServiceImpl(GameSnapshotRepository gameSnapshotRepository,
                                BattleEventRepository battleEventRepository,
                                com.tfm.db_back.domain.repository.CharacterRepository characterRepository,
                                com.tfm.db_back.domain.repository.GameRepository gameRepository,
                                com.tfm.db_back.domain.repository.UserRepository userRepository) {
        this.gameSnapshotRepository = gameSnapshotRepository;
        this.battleEventRepository = battleEventRepository;
        this.characterRepository = characterRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @Override
    public com.tfm.db_back.api.dto.UserStatsResponseDto getUserStats(java.util.UUID userId) {
        List<com.tfm.db_back.domain.model.Character> characters = characterRepository.findByUserId(userId);
        List<String> charIds = characters.stream()
                .map(c -> c.getId().toString())
                .collect(Collectors.toList());

        if (charIds.isEmpty()) {
            return new com.tfm.db_back.api.dto.UserStatsResponseDto(0, 0, 0, 0, 0, 0, 0);
        }

        // Obtener todos los snapshots donde participó el usuario
        List<GameSnapshotDocument> allSnapshots = gameSnapshotRepository.findByPlayersCharacterIdIn(charIds);
        
        // Agrupar por gameId y quedarnos con el último snapshot de cada partida
        java.util.Map<String, GameSnapshotDocument> latestSnapshotsPerGame = allSnapshots.stream()
                .collect(Collectors.toMap(
                        GameSnapshotDocument::getGameId,
                        s -> s,
                        (s1, s2) -> s1.getSnapshotAt().isAfter(s2.getSnapshotAt()) ? s1 : s2
                ));

        // Victorias reales desde PostgreSQL (partidas ganadas)
        List<java.util.UUID> charUuids = characters.stream().map(com.tfm.db_back.domain.model.Character::getId).collect(Collectors.toList());
        long totalWins = gameRepository.countByWinnerCharacterIdIn(charUuids);

        long totalAttacks = 0;
        long totalTroopsLost = 0;
        long totalTrained = 0;
        long totalCreditsEarned = 0;
        long totalTroopsDeployed = 0;
        long totalPlayTimeMs = 0;

        for (GameSnapshotDocument snapshot : latestSnapshotsPerGame.values()) {
            GameSnapshotDocument.PlayerSnapshot player = snapshot.getPlayers().stream()
                    .filter(p -> charIds.contains(p.getCharacterId()))
                    .findFirst()
                    .orElse(null);

            if (player != null && player.getStats() != null) {
                GameSnapshotDocument.ParticipantStats stats = player.getStats();
                totalTrained += stats.getTotalTroopsTrained();
                totalCreditsEarned += stats.getTotalEconomicCreditsEarned();
                totalTroopsLost += stats.getTotalTroopsLost();
                totalAttacks += stats.getTotalAttacksLaunched();
                totalTroopsDeployed += stats.getTotalTroopsDeployed();
                totalPlayTimeMs += stats.getTimePlayedMs();
            }
        }

        return new com.tfm.db_back.api.dto.UserStatsResponseDto(
                totalWins,
                totalAttacks,
                totalTroopsLost,
                totalTrained,
                totalCreditsEarned,
                totalPlayTimeMs / 60000, // Convertir Ms a Minutos
                totalTroopsDeployed
        );
    }

    @Override
    public com.tfm.db_back.api.dto.UserStatsResponseDto getGameStats(java.util.UUID gameId, java.util.UUID userId) {
        List<com.tfm.db_back.domain.model.Character> characters = characterRepository.findByUserId(userId);
        List<String> charIds = characters.stream()
                .map(c -> c.getId().toString())
                .collect(Collectors.toList());

        if (charIds.isEmpty()) {
            return new com.tfm.db_back.api.dto.UserStatsResponseDto(0, 0, 0, 0, 0, 0, 0);
        }

        return gameSnapshotRepository.findFirstByGameIdOrderBySnapshotAtDesc(gameId.toString())
                .map(snapshot -> {
                    GameSnapshotDocument.PlayerSnapshot player = snapshot.getPlayers().stream()
                            .filter(p -> charIds.contains(p.getCharacterId()))
                            .findFirst()
                            .orElse(null);

                    if (player == null || player.getStats() == null) {
                        return new com.tfm.db_back.api.dto.UserStatsResponseDto(0, 0, 0, 0, 0, 0, 0);
                    }

                    GameSnapshotDocument.ParticipantStats stats = player.getStats();

                    // Verificar si ganó la partida en PostgreSQL
                    long totalWins = gameRepository.findById(gameId)
                            .map(g -> g.getWinnerCharacterId() != null && charIds.contains(g.getWinnerCharacterId().toString()) ? 1L : 0L)
                            .orElse(0L);

                    return new com.tfm.db_back.api.dto.UserStatsResponseDto(
                            totalWins,
                            stats.getTotalAttacksLaunched(),
                            stats.getTotalTroopsLost(),
                            stats.getTotalTroopsTrained(),
                            stats.getTotalEconomicCreditsEarned(),
                            stats.getTimePlayedMs() / 60000,
                            stats.getTotalTroopsDeployed()
                    );
                })
                .orElse(new com.tfm.db_back.api.dto.UserStatsResponseDto(0, 0, 0, 0, 0, 0, 0));
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

            // Recalcular Gloria Eterna para todos los participantes de la partida
            if (document.getPlayers() != null) {
                for (GameSnapshotDocument.PlayerSnapshot player : document.getPlayers()) {
                    try {
                        java.util.UUID charId = java.util.UUID.fromString(player.getCharacterId());
                        characterRepository.findById(charId).ifPresent(character -> {
                            java.util.UUID userId = character.getUserId();
                            recalculateUserGloriaEterna(userId);
                        });
                    } catch (Exception ex) {
                        log.error("Failed to update Gloria Eterna for character: {}", player.getCharacterId(), ex);
                    }
                }
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
                            p.eliminated(),
                            p.stats() == null ? null : new GameSnapshotDocument.ParticipantStats(
                                    p.stats().totalEconomicCreditsEarned(),
                                    p.stats().totalResearchCreditsEarned(),
                                    p.stats().totalTroopsTrained(),
                                    p.stats().totalAttacksLaunched(),
                                    p.stats().totalDamageDealt(),
                                    p.stats().totalDamageReceived(),
                                    p.stats().totalTroopsLost(),
                                    p.stats().totalTroopsDeployed(),
                                    p.stats().timePlayedMs()
                            )
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

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void recalculateUserGloriaEterna(java.util.UUID userId) {
        com.tfm.db_back.api.dto.UserStatsResponseDto stats = getUserStats(userId);
        long wins = stats.totalWins();
        long attacks = stats.totalAttacks();
        long credits = stats.totalCreditsEarned();

        long gloria = (wins * 1000) + (attacks * 50) + (credits / 10);
        final int gloriaInt = gloria < 0 ? 0 : (int) gloria;

        userRepository.findById(userId).ifPresent(user -> {
            user.setGloriaEterna(gloriaInt);
            userRepository.save(user);
            log.info("[Ranking] Recalculated Gloria Eterna for user {}: {} (Wins: {}, Attacks: {}, Credits: {})",
                    user.getUsername(), gloriaInt, wins, attacks, credits);
        });
    }

    /**
     * Inicializador de inicio que calcula retrospectivamente la Gloria Eterna
     * para todos los usuarios leyendo de MongoDB y PostgreSQL.
     */
    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void initGloriaEterna() {
        log.info("[Ranking] Iniciando recalculación retrospectiva de Gloria Eterna para todos los usuarios...");
        List<com.tfm.db_back.domain.model.User> users = userRepository.findAll();
        for (com.tfm.db_back.domain.model.User user : users) {
            try {
                recalculateUserGloriaEterna(user.getId());
            } catch (Exception e) {
                log.error("[Ranking] Error al inicializar Gloria Eterna para el usuario: {}", user.getUsername(), e);
            }
        }
        log.info("[Ranking] Recalculación retrospectiva de Gloria Eterna completada con éxito.");
    }
}
