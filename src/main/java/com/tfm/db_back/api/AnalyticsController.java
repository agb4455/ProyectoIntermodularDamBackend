package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;
import com.tfm.db_back.api.dto.ApiResponse;
import com.tfm.db_back.api.dto.UserStatsResponseDto;
import com.tfm.db_back.domain.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para la recepción de datos de analítica.
 * Procesa instantáneas del estado del juego y eventos de batalla para su almacenamiento en MongoDB.
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
@RestController
@RequestMapping("/internal/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/snapshots")
    public ResponseEntity<Void> publishSnapshot(@Valid @RequestBody AnalyticsSnapshotRequestDto dto) {
        analyticsService.saveSnapshot(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserStatsResponseDto>> getUserStats(@PathVariable UUID userId) {
        UserStatsResponseDto stats = analyticsService.getUserStats(userId);
        return ResponseEntity.ok(new ApiResponse<>(stats));
    }

    @GetMapping("/game/{gameId}/user/{userId}")
    public ResponseEntity<ApiResponse<UserStatsResponseDto>> getGameStats(@PathVariable UUID gameId, @PathVariable UUID userId) {
        UserStatsResponseDto stats = analyticsService.getGameStats(gameId, userId);
        return ResponseEntity.ok(new ApiResponse<>(stats));
    }
}
