package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.CreateGameRequestDto;
import com.tfm.db_back.api.dto.EndGameRequestDto;
import com.tfm.db_back.api.dto.GameResponseDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.Game;
import com.tfm.db_back.domain.model.GameStatus;
import com.tfm.db_back.domain.model.GameParticipant;
import com.tfm.db_back.domain.repository.GameParticipantRepository;
import com.tfm.db_back.domain.repository.GameRepository;
import com.tfm.db_back.domain.repository.GameStateDumpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de ciclo de vida de partidas.
 * Gestiona creación, consulta y finalización de partidas en PostgreSQL.
 */
@Service
public class GameServiceImpl implements GameService {

    // Eliminamos las constantes de String en favor del Enum

    private final GameRepository gameRepository;
    private final GameParticipantRepository participantRepository;
    private final GameStateDumpRepository dumpRepository;

    // Inyección por constructor — sin @Autowired ni Lombok (java_good_practices.md)
    public GameServiceImpl(GameRepository gameRepository,
                           GameParticipantRepository participantRepository,
                           GameStateDumpRepository dumpRepository) {
        this.gameRepository = gameRepository;
        this.participantRepository = participantRepository;
        this.dumpRepository = dumpRepository;
    }

    /**
     * Crea una nueva partida y registra sus participantes en orden de entrada.
     * La partida se crea con estado "waiting" — el Middle la transiciona a "preparation".
     */
    @Override
    @Transactional
    public GameResponseDto createGame(CreateGameRequestDto dto) {
        // Crear el registro principal de la partida
        Game game = new Game(GameStatus.WAITING, dto.maxPlayers());
        Game savedGame = gameRepository.save(game);

        // Registrar participantes en orden (join_order basado en índice de la lista)
        List<UUID> characterIds = dto.characterIds();
        for (int i = 0; i < characterIds.size(); i++) {
            GameParticipant participant = new GameParticipant(
                    savedGame.getId(),
                    characterIds.get(i),
                    (short) (i + 1) // join_order es 1-based
            );
            participantRepository.save(participant);
        }

        // Cargar los participantes recién guardados para mapear la respuesta
        List<GameParticipant> participants = participantRepository.findByGameId(savedGame.getId());
        return mapToResponseDto(savedGame, participants, null);
    }

    /**
     * Recupera una partida por UUID incluyendo su último state dump.
     * Es el endpoint que usa el Middle en el arranque para recuperar el estado.
     */
    @Override
    @Transactional(readOnly = true)
    public GameResponseDto getGame(UUID id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida no encontrada con id: " + id));

        List<GameParticipant> participants = participantRepository.findByGameId(id);

        // Recuperar el último volcado disponible (puede ser null si no hay ninguno aún)
        String latestStateJson = dumpRepository
                .findFirstByGameIdOrderByDumpedAtDesc(id)
                .map(dump -> dump.getStateJson())
                .orElse(null);

        return mapToResponseDto(game, participants, latestStateJson);
    }

    /**
     * Lista todas las partidas activas (status != 'finished').
     * Usado por el Middle en cada reinicio para recuperar el estado de todas las partidas.
     * La query usa el índice en games(status) para evitar seq scan.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GameResponseDto> getActiveGames() {
        List<Game> activeGames = gameRepository.findByStatusNot(GameStatus.FINISHED);

        return activeGames.stream()
                .map(game -> {
                    List<GameParticipant> participants = participantRepository.findByGameId(game.getId());
                    String latestStateJson = dumpRepository
                            .findFirstByGameIdOrderByDumpedAtDesc(game.getId())
                            .map(dump -> dump.getStateJson())
                            .orElse(null);
                    return mapToResponseDto(game, participants, latestStateJson);
                })
                .toList();
    }

    /**
     * Finaliza una partida: status → 'finished', registra ganador y ended_at.
     * El Middle llama a este endpoint cuando la partida termina.
     */
    @Override
    @Transactional
    public void endGame(UUID id, EndGameRequestDto dto) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida no encontrada con id: " + id));

        game.setStatus(GameStatus.FINISHED);
        game.setEndedAt(Instant.now());
        game.setWinnerCharacterId(dto.winnerCharacterId()); // puede ser null (empate)

        gameRepository.save(game);
    }

    // --- Métodos privados de mapeo ---

    private GameResponseDto mapToResponseDto(Game game,
                                              List<GameParticipant> participants,
                                              String latestStateJson) {
        List<GameResponseDto.ParticipantDto> participantDtos = participants.stream()
                .map(p -> new GameResponseDto.ParticipantDto(
                        p.getId(),
                        p.getCharacterId(),
                        p.getJoinOrder(),
                        p.isEliminated()
                ))
                .toList();

        return new GameResponseDto(
                game.getId(),
                game.getStatus(),
                game.getMaxPlayers(),
                game.getCreatedAt(),
                game.getStartedAt(),
                game.getEndedAt(),
                game.getWinnerCharacterId(),
                participantDtos,
                latestStateJson
        );
    }
}
