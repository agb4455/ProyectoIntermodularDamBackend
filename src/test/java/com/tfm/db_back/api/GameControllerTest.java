package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.CreateGameRequestDto;
import com.tfm.db_back.api.dto.EndGameRequestDto;
import com.tfm.db_back.api.dto.GameResponseDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.service.GameDumpService;
import com.tfm.db_back.domain.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de capa web para GameController.
 * Usa standaloneSetup para aislar el controlador del contexto Spring completo
 * y evitar problemas con Spring Security en entorno de test (patrón establecido en sprints 1-3).
 * Patrón: methodName_givenContext_shouldExpectedBehavior
 */
@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @Mock
    private GameDumpService gameDumpService;

    @InjectMocks
    private GameController gameController;

    private UUID gameId;
    private UUID charId;
    private GameResponseDto testGameResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        gameId = UUID.randomUUID();
        charId = UUID.randomUUID();

        GameResponseDto.ParticipantDto participant = new GameResponseDto.ParticipantDto(
                UUID.randomUUID(), charId, (short) 1, false
        );

        testGameResponse = new GameResponseDto(
                gameId, "waiting", (short) 2,
                Instant.now(), null, null, null,
                List.of(participant), null
        );
    }

    // --- POST /internal/games ---

    @Test
    void createGame_givenValidDto_shouldReturn201WithGameResponse() throws Exception {
        when(gameService.createGame(any(CreateGameRequestDto.class))).thenReturn(testGameResponse);

        String payload = """
                {
                    "maxPlayers": 2,
                    "characterIds": ["%s", "%s"]
                }
                """.formatted(charId, UUID.randomUUID());

        mockMvc.perform(post("/internal/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(gameId.toString()))
                .andExpect(jsonPath("$.data.status").value("waiting"))
                .andExpect(jsonPath("$.data.participants").isArray());

        verify(gameService).createGame(any(CreateGameRequestDto.class));
    }

    @Test
    void createGame_givenInvalidMaxPlayers_shouldReturn400() throws Exception {
        String payload = """
                {
                    "maxPlayers": 1,
                    "characterIds": ["%s"]
                }
                """.formatted(charId);

        mockMvc.perform(post("/internal/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(gameService);
    }

    @Test
    void createGame_givenEmptyCharacterList_shouldReturn400() throws Exception {
        String payload = """
                {
                    "maxPlayers": 2,
                    "characterIds": []
                }
                """;

        mockMvc.perform(post("/internal/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(gameService);
    }

    // --- GET /internal/games/active ---

    @Test
    void getActiveGames_givenActiveGames_shouldReturn200WithList() throws Exception {
        when(gameService.getActiveGames()).thenReturn(List.of(testGameResponse));

        mockMvc.perform(get("/internal/games/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(gameId.toString()));
    }

    @Test
    void getActiveGames_givenNoActiveGames_shouldReturn200WithEmptyList() throws Exception {
        when(gameService.getActiveGames()).thenReturn(List.of());

        mockMvc.perform(get("/internal/games/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // --- GET /internal/games/{id} ---

    @Test
    void getGame_givenExistingId_shouldReturn200WithGameResponse() throws Exception {
        when(gameService.getGame(gameId)).thenReturn(testGameResponse);

        mockMvc.perform(get("/internal/games/{id}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(gameId.toString()))
                .andExpect(jsonPath("$.data.status").value("waiting"));
    }

    @Test
    void getGame_givenNonExistingId_shouldReturn404() throws Exception {
        when(gameService.getGame(gameId))
                .thenThrow(new EntityNotFoundException("Partida no encontrada"));

        mockMvc.perform(get("/internal/games/{id}", gameId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    // --- PUT /internal/games/{id}/state ---

    @Test
    void dumpState_givenValidPayload_shouldReturn204() throws Exception {
        doNothing().when(gameDumpService).dumpState(eq(gameId), any());

        String payload = """
                {
                    "stateJson": "{\\"phase\\":\\"war\\"}"
                }
                """;

        mockMvc.perform(put("/internal/games/{id}/state", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNoContent());

        verify(gameDumpService).dumpState(eq(gameId), any());
    }

    @Test
    void dumpState_givenBlankStateJson_shouldReturn400() throws Exception {
        String payload = """
                {
                    "stateJson": ""
                }
                """;

        mockMvc.perform(put("/internal/games/{id}/state", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(gameDumpService);
    }

    @Test
    void dumpState_givenNonExistingGameId_shouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Partida no encontrada"))
                .when(gameDumpService).dumpState(eq(gameId), any());

        String payload = """
                {
                    "stateJson": "{\\"phase\\":\\"war\\"}"
                }
                """;

        mockMvc.perform(put("/internal/games/{id}/state", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    // --- POST /internal/games/{id}/end ---

    @Test
    void endGame_givenExistingGame_shouldReturn200WithFinishedGame() throws Exception {
        GameResponseDto finishedGame = new GameResponseDto(
                gameId, "finished", (short) 2,
                Instant.now(), Instant.now(), Instant.now(), charId,
                List.of(), null
        );

        doNothing().when(gameService).endGame(eq(gameId), any(EndGameRequestDto.class));
        when(gameService.getGame(gameId)).thenReturn(finishedGame);

        String payload = """
                {
                    "winnerCharacterId": "%s"
                }
                """.formatted(charId);

        mockMvc.perform(post("/internal/games/{id}/end", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("finished"))
                .andExpect(jsonPath("$.data.winnerCharacterId").value(charId.toString()));
    }

    @Test
    void endGame_givenNullWinner_shouldReturn200WithoutWinner() throws Exception {
        GameResponseDto finishedGame = new GameResponseDto(
                gameId, "finished", (short) 2,
                Instant.now(), Instant.now(), Instant.now(), null,
                List.of(), null
        );

        doNothing().when(gameService).endGame(eq(gameId), any(EndGameRequestDto.class));
        when(gameService.getGame(gameId)).thenReturn(finishedGame);

        String payload = """
                {
                    "winnerCharacterId": null
                }
                """;

        mockMvc.perform(post("/internal/games/{id}/end", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("finished"))
                .andExpect(jsonPath("$.data.winnerCharacterId").doesNotExist());
    }

    @Test
    void endGame_givenNonExistingId_shouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Partida no encontrada"))
                .when(gameService).endGame(eq(gameId), any(EndGameRequestDto.class));

        String payload = """
                {
                    "winnerCharacterId": null
                }
                """;

        mockMvc.perform(post("/internal/games/{id}/end", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
