package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;
import com.tfm.db_back.infrastructure.mongodb.BattleEventRepository;
import com.tfm.db_back.infrastructure.mongodb.GameSnapshotDocument;
import com.tfm.db_back.infrastructure.mongodb.GameSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {

    private GameSnapshotRepository gameSnapshotRepository;
    private BattleEventRepository battleEventRepository;
    private AnalyticsServiceImpl analyticsService;

    @BeforeEach
    void setUp() {
        gameSnapshotRepository = mock(GameSnapshotRepository.class);
        battleEventRepository = mock(BattleEventRepository.class);
        analyticsService = new AnalyticsServiceImpl(gameSnapshotRepository, battleEventRepository);
    }

    @Test
    void saveSnapshot_givenValidDto_shouldSaveDocumentToMongoDB() {
        AnalyticsSnapshotRequestDto dto = new AnalyticsSnapshotRequestDto(
                UUID.randomUUID().toString(),
                "2026-04-21T18:00:00Z",
                "war",
                List.of(
                        new AnalyticsSnapshotRequestDto.PlayerSnapshotDto(
                                "char1", "clan1", 100, 50, 1000,
                                List.of(new AnalyticsSnapshotRequestDto.TroopSnapshotDto("troop1", "typeA", 100, true)),
                                List.of("research1"),
                                false
                        )
                ),
                List.of()
        );

        when(gameSnapshotRepository.save(any(GameSnapshotDocument.class))).thenReturn(new GameSnapshotDocument());

        CompletableFuture<Void> future = analyticsService.saveSnapshot(dto);
        future.join();

        ArgumentCaptor<GameSnapshotDocument> captor = ArgumentCaptor.forClass(GameSnapshotDocument.class);
        verify(gameSnapshotRepository).save(captor.capture());

        GameSnapshotDocument savedDoc = captor.getValue();
        assertThat(savedDoc.getGameId()).isEqualTo(dto.gameId());
        assertThat(savedDoc.getPhase()).isEqualTo("war");
        assertThat(savedDoc.getPlayers()).hasSize(1);
        assertThat(savedDoc.getPlayers().get(0).getCharacterId()).isEqualTo("char1");
        assertThat(savedDoc.getPlayers().get(0).getTroops()).hasSize(1);
        assertThat(savedDoc.getPlayers().get(0).getTroops().get(0).getTroopId()).isEqualTo("troop1");
    }

    @Test
    void saveSnapshot_givenMongoFailure_shouldLogErrorAndNotPropagate() {
        AnalyticsSnapshotRequestDto dto = new AnalyticsSnapshotRequestDto(
                UUID.randomUUID().toString(),
                "2026-04-21T18:00:00Z",
                "war",
                List.of(),
                List.of()
        );

        when(gameSnapshotRepository.save(any(GameSnapshotDocument.class)))
                .thenThrow(new RuntimeException("Mongo connection refused"));

        assertThatCode(() -> analyticsService.saveSnapshot(dto).join())
                .doesNotThrowAnyException();

        verify(gameSnapshotRepository).save(any());
    }
}
