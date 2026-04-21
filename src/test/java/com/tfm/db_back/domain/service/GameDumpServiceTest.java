package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.StateDumpRequestDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.GameStateDump;
import com.tfm.db_back.domain.repository.GameRepository;
import com.tfm.db_back.domain.repository.GameStateDumpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GameDumpServiceImpl.
 * Patrón: methodName_givenContext_shouldExpectedBehavior
 * Sin contexto Spring — solo JUnit 5 + Mockito.
 */
@ExtendWith(MockitoExtension.class)
class GameDumpServiceTest {

    @Mock
    private GameStateDumpRepository dumpRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameDumpServiceImpl gameDumpService;

    private UUID gameId;
    private static final String SAMPLE_STATE_JSON = "{\"phase\":\"war\",\"players\":{}}";

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
    }

    // --- dumpState ---

    @Test
    void dumpState_givenExistingGame_shouldInsertNewDump() {
        StateDumpRequestDto dto = new StateDumpRequestDto(SAMPLE_STATE_JSON);

        when(gameRepository.existsById(gameId)).thenReturn(true);
        when(dumpRepository.save(any(GameStateDump.class))).thenAnswer(inv -> inv.getArgument(0));

        gameDumpService.dumpState(gameId, dto);

        // Verificar que se guardó un nuevo dump con el json correcto
        verify(dumpRepository).save(argThat(dump ->
                gameId.equals(dump.getGameId()) &&
                SAMPLE_STATE_JSON.equals(dump.getStateJson())
        ));
    }

    @Test
    void dumpState_givenNonExistingGame_shouldThrowEntityNotFoundException() {
        when(gameRepository.existsById(gameId)).thenReturn(false);

        StateDumpRequestDto dto = new StateDumpRequestDto(SAMPLE_STATE_JSON);

        assertThatThrownBy(() -> gameDumpService.dumpState(gameId, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(gameId.toString());

        // No se debe guardar ningún dump si la partida no existe
        verifyNoInteractions(dumpRepository);
    }

    @Test
    void dumpState_givenMultipleCalls_shouldInsertMultipleRows() {
        // Verificar que cada llamada genera un INSERT independiente (no un UPDATE)
        when(gameRepository.existsById(gameId)).thenReturn(true);
        when(dumpRepository.save(any(GameStateDump.class))).thenAnswer(inv -> inv.getArgument(0));

        gameDumpService.dumpState(gameId, new StateDumpRequestDto("{\"phase\":\"preparation\"}"));
        gameDumpService.dumpState(gameId, new StateDumpRequestDto("{\"phase\":\"war\"}"));
        gameDumpService.dumpState(gameId, new StateDumpRequestDto("{\"phase\":\"end\"}"));

        // Debe haberse llamado 3 veces al save — historial preservado
        verify(dumpRepository, times(3)).save(any(GameStateDump.class));
    }

    // --- getLatestDump ---

    @Test
    void getLatestDump_givenExistingDump_shouldReturnLatestStateJson() {
        GameStateDump dump = new GameStateDump(UUID.randomUUID(), gameId, SAMPLE_STATE_JSON, Instant.now());
        when(dumpRepository.findFirstByGameIdOrderByDumpedAtDesc(gameId)).thenReturn(Optional.of(dump));

        String result = gameDumpService.getLatestDump(gameId);

        assertThat(result).isEqualTo(SAMPLE_STATE_JSON);
    }

    @Test
    void getLatestDump_givenNoDumps_shouldReturnNull() {
        when(dumpRepository.findFirstByGameIdOrderByDumpedAtDesc(gameId)).thenReturn(Optional.empty());

        String result = gameDumpService.getLatestDump(gameId);

        assertThat(result).isNull();
    }
}
