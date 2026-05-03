package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;

import java.util.concurrent.CompletableFuture;

/**
 * Contrato del servicio de analíticas.
 * Define las operaciones para el almacenamiento asíncrono de datos en MongoDB.
 *
 * @author Adriana Cabaleiro Álvarez
 */
public interface AnalyticsService {
    CompletableFuture<Void> saveSnapshot(AnalyticsSnapshotRequestDto dto);
}
