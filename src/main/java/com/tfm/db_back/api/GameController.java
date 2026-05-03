package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.ApiResponse;
import com.tfm.db_back.api.dto.CreateGameRequestDto;
import com.tfm.db_back.api.dto.EndGameRequestDto;
import com.tfm.db_back.api.dto.GameResponseDto;
import com.tfm.db_back.api.dto.StateDumpRequestDto;
import com.tfm.db_back.domain.service.GameDumpService;
import com.tfm.db_back.domain.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para el dominio de partidas.
 * Expone los 5 endpoints de gestión del ciclo de vida de partidas.
 * Todos los endpoints requieren el handshake JWT (validado por HandshakeJwtFilter).
 * NUNCA devuelve entidades JPA — siempre usa GameResponseDto envuelto en ApiResponse.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@RestController
@RequestMapping("/internal/games")
public class GameController {

    private final GameService gameService;
    private final GameDumpService gameDumpService;

    // Inyección por constructor explícita — sin @Autowired ni Lombok (java_good_practices.md)
    public GameController(GameService gameService, GameDumpService gameDumpService) {
        this.gameService = gameService;
        this.gameDumpService = gameDumpService;
    }

    /**
     * POST /internal/games
     * Crea un nuevo registro de partida con sus participantes.
     * El Middle llama a este endpoint al iniciar una nueva partida en su memoria.
     * Devuelve 201 Created + ApiResponse<GameResponseDto>.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GameResponseDto>> createGame(
            @Valid @RequestBody CreateGameRequestDto dto) {
        GameResponseDto created = gameService.createGame(dto);
        return ResponseEntity.status(201).body(new ApiResponse<>(created));
    }

    /**
     * GET /internal/games/active
     * Lista todas las partidas con status != 'finished'.
     * IMPORTANTE: Esta ruta debe estar ANTES de /{id} para evitar que Spring
     * interprete "active" como un UUID.
     * Usado por el Middle en cada reinicio para recuperar el estado de juego.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<GameResponseDto>>> getActiveGames() {
        List<GameResponseDto> active = gameService.getActiveGames();
        return ResponseEntity.ok(new ApiResponse<>(active));
    }

    /**
     * GET /internal/games/{id}
     * Recupera una partida por UUID incluyendo el último state dump.
     * Devuelve 200 OK + ApiResponse<GameResponseDto>, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameResponseDto>> getGame(@PathVariable UUID id) {
        GameResponseDto game = gameService.getGame(id);
        return ResponseEntity.ok(new ApiResponse<>(game));
    }

    /**
     * PUT /internal/games/{id}/state
     * Inserta un nuevo volcado del estado de la partida en game_state_dumps.
     * El Middle llama a este endpoint cada ~15 minutos (POSTGRES_DUMP_INTERVAL_MS).
     * El stateJson es un String opaco — el DB Server nunca lo procesa.
     * Devuelve 204 No Content al completar el INSERT.
     */
    @PutMapping("/{id}/state")
    public ResponseEntity<Void> dumpState(@PathVariable UUID id,
                                          @Valid @RequestBody StateDumpRequestDto dto) {
        gameDumpService.dumpState(id, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /internal/games/{id}/end
     * Marca la partida como finalizada y registra al ganador.
     * Devuelve 200 OK + ApiResponse<GameResponseDto> con status='finished'.
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<GameResponseDto>> endGame(
            @PathVariable UUID id,
            @RequestBody EndGameRequestDto dto) {
        gameService.endGame(id, dto);
        GameResponseDto finished = gameService.getGame(id);
        return ResponseEntity.ok(new ApiResponse<>(finished));
    }
}
