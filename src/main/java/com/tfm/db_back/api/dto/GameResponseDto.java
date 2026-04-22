package com.tfm.db_back.api.dto;

import com.tfm.db_back.domain.model.GameStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para una partida.
 * Incluye metadatos de la entidad Game, la lista de participantes
 * y el último state_json conocido (puede ser null si no hay ningún volcado aún).
 * NUNCA expone la entidad JPA directamente.
 */
public record GameResponseDto(
        UUID id,
        GameStatus status,
        short maxPlayers,
        Instant createdAt,
        Instant startedAt,
        Instant endedAt,
        UUID winnerCharacterId,
        List<ParticipantDto> participants,
        // Último volcado de estado — null si la partida acaba de ser creada y no hay dumps aún
        String latestStateJson
) {

    /**
     * Sub-DTO para representar a cada participante dentro de GameResponseDto.
     * Evita referencias cruzadas con la entidad JPA GameParticipant.
     */
    public record ParticipantDto(
            UUID id,
            UUID characterId,
            short joinOrder,
            boolean eliminated
    ) {
    }
}
