package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.StateDumpRequestDto;
import com.tfm.db_back.domain.exception.EntityNotFoundException;
import com.tfm.db_back.domain.model.GameStateDump;
import com.tfm.db_back.domain.repository.GameRepository;
import com.tfm.db_back.domain.repository.GameStateDumpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación del servicio de volcados de estado de partida.
 * REGLA CRÍTICA: Solo se realizan INSERTs en game_state_dumps.
 * El historial completo se conserva — nunca UPDATE ni DELETE.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Service
public class GameDumpServiceImpl implements GameDumpService {

    private final GameStateDumpRepository dumpRepository;
    private final GameRepository gameRepository;

    // Inyección por constructor — sin @Autowired ni Lombok (java_good_practices.md)
    public GameDumpServiceImpl(GameStateDumpRepository dumpRepository,
                               GameRepository gameRepository) {
        this.dumpRepository = dumpRepository;
        this.gameRepository = gameRepository;
    }

    /**
     * Inserta un nuevo volcado de estado en game_state_dumps.
     * Verifica que la partida existe antes de persistir.
     * El stateJson se persiste tal cual como String opaco — nunca se deserializa.
     */
    @Override
    @Transactional
    public void dumpState(UUID gameId, StateDumpRequestDto dto) {
        // Verificar que la partida existe antes de crear el dump
        if (!gameRepository.existsById(gameId)) {
            throw new EntityNotFoundException("Partida no encontrada con id: " + gameId);
        }

        GameStateDump dump = new GameStateDump(gameId, dto.stateJson());
        dumpRepository.save(dump);
    }

    /**
     * Recupera el string JSON del volcado más reciente para una partida.
     * Devuelve null si no existe ningún volcado (partida recién creada).
     */
    @Override
    @Transactional(readOnly = true)
    public String getLatestDump(UUID gameId) {
        return dumpRepository
                .findFirstByGameIdOrderByDumpedAtDesc(gameId)
                .map(GameStateDump::getStateJson)
                .orElse(null);
    }
}
