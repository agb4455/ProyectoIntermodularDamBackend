package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;
import com.tfm.db_back.domain.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.accepted().build();
    }
}
