package com.tfm.db_back.domain.service;

import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;

import java.util.concurrent.CompletableFuture;

public interface AnalyticsService {
    CompletableFuture<Void> saveSnapshot(AnalyticsSnapshotRequestDto dto);
}
