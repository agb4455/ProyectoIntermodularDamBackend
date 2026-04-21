package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateGameRequestDto;
import com.tfm.db_back.api.dto.EndGameRequestDto;
import com.tfm.db_back.api.dto.GameResponseDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.Game;
import com.tfm.db_back.domain.model.GameParticipant;
import com.tfm.db_back.domain.model.GameStateDump;
import com.tfm.db_back.domain.repository.GameParticipantRepository;
import com.tfm.db_back.domain.repository.GameRepository;
import com.tfm.db_back.domain.repository.GameStateDumpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GameServiceImpl.
 * Patrón: methodName_givenContext_shouldExpectedBehavior
 * Sin contexto Spring — solo JUnit 5 + Mockito.
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameParticipantRepository participantRepository;

    @Mock
    private GameStateDumpRepository dumpRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    private UUID gameId;
    private UUID char1Id;
    private UUID char2Id;
    private Game testGame;
    private GameParticipant participant1;
    private GameParticipant participant2;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        char1Id = UUID.randomUUID();
        char2Id = UUID.randomUUID();

        testGame = new Game(gameId, "waiting", (short) 2,
                Instant.now(), null, null, null);

        participant1 = new GameParticipant(UUID.randomUUID(), gameId, char1Id, (short) 1, false);
        participant2 = new GameParticipant(UUID.randomUUID(), gameId, char2Id, (short) 2, false);
    }

    // --- createGame ---

    @Test
    void createGame_givenValidDto_shouldPersistGameAndParticipants() {
        CreateGameRequestDto dto = new CreateGameRequestDto((short) 2, List.of(char1Id, char2Id));

        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(participantRepository.findByGameId(gameId)).thenReturn(List.of(participant1, participant2));

        GameResponseDto result = gameService.createGame(dto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(gameId);
        assertThat(result.status()).isEqualTo("waiting");
        assertThat(result.participants()).hasSize(2);
        assertThat(result.latestStateJson()).isNull(); // sin dump aún

        verify(gameRepository).save(any(Game.class));
        verify(participantRepository, times(2)).save(any(GameParticipant.class));
    }

    @Test
    void createGame_givenValidDto_shouldSetJoinOrderStartingAtOne() {
        CreateGameRequestDto dto = new CreateGameRequestDto((short) 2, List.of(char1Id, char2Id));

        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(participantRepository.findByGameId(gameId)).thenReturn(List.of(participant1, participant2));

        GameResponseDto result = gameService.createGame(dto);

        // Verificar que los participantes se guardaron con join_order correcto
        assertThat(result.participants().get(0).joinOrder()).isEqualTo((short) 1);
        assertThat(result.participants().get(1).joinOrder()).isEqualTo((short) 2);
    }

    // --- getGame ---

    @Test
    void getGame_givenExistingId_shouldReturnGameWithLatestDump() {
        GameStateDump dump = new GameStateDump(UUID.randomUUID(), gameId, "{\"phase\":\"war\"}", Instant.now());

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(participantRepository.findByGameId(gameId)).thenReturn(List.of(participant1));
        when(dumpRepository.findFirstByGameIdOrderByDumpedAtDesc(gameId)).thenReturn(Optional.of(dump));

        GameResponseDto result = gameService.getGame(gameId);

        assertThat(result.id()).isEqualTo(gameId);
        assertThat(result.latestStateJson()).isEqualTo("{\"phase\":\"war\"}");
    }

    @Test
    void getGame_givenExistingIdWithNoDump_shouldReturnNullStateJson() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(participantRepository.findByGameId(gameId)).thenReturn(List.of(participant1));
        when(dumpRepository.findFirstByGameIdOrderByDumpedAtDesc(gameId)).thenReturn(Optional.empty());

        GameResponseDto result = gameService.getGame(gameId);

        assertThat(result.latestStateJson()).isNull();
    }

    @Test
    void getGame_givenNonExistingId_shouldThrowEntityNotFoundException() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGame(gameId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(gameId.toString());
    }

    // --- getActiveGames ---

    @Test
    void getActiveGames_givenActiveGames_shouldReturnListExcludingFinished() {
        when(gameRepository.findByStatusNot("finished")).thenReturn(List.of(testGame));
        when(participantRepository.findByGameId(gameId)).thenReturn(List.of(participant1));
        when(dumpRepository.findFirstByGameIdOrderByDumpedAtDesc(gameId)).thenReturn(Optional.empty());

        List<GameResponseDto> result = gameService.getActiveGames();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("waiting");
        verify(gameRepository).findByStatusNot("finished");
    }

    @Test
    void getActiveGames_givenNoActiveGames_shouldReturnEmptyList() {
        when(gameRepository.findByStatusNot("finished")).thenReturn(List.of());

        List<GameResponseDto> result = gameService.getActiveGames();

        assertThat(result).isEmpty();
    }

    // --- endGame ---

    @Test
    void endGame_givenExistingGame_shouldSetStatusFinishedAndWinner() {
        EndGameRequestDto dto = new EndGameRequestDto(char1Id);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        gameService.endGame(gameId, dto);

        verify(gameRepository).save(argThat(g ->
                "finished".equals(g.getStatus()) &&
                char1Id.equals(g.getWinnerCharacterId()) &&
                g.getEndedAt() != null
        ));
    }

    @Test
    void endGame_givenNullWinner_shouldSetStatusFinishedWithNullWinner() {
        EndGameRequestDto dto = new EndGameRequestDto(null);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        gameService.endGame(gameId, dto);

        verify(gameRepository).save(argThat(g ->
                "finished".equals(g.getStatus()) &&
                g.getWinnerCharacterId() == null
        ));
    }

    @Test
    void endGame_givenNonExistingId_shouldThrowEntityNotFoundException() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.endGame(gameId, new EndGameRequestDto(null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(gameId.toString());
    }
}
