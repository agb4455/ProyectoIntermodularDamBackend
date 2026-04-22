package com.tfm.db_back.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;
import com.tfm.db_back.domain.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalyticsControllerTest {

    private MockMvc mockMvc;
    private AnalyticsService analyticsService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        analyticsService = mock(AnalyticsService.class);
        objectMapper = new ObjectMapper();
        
        AnalyticsController controller = new AnalyticsController(analyticsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void publishSnapshot_givenValidDto_shouldReturn202() throws Exception {
        AnalyticsSnapshotRequestDto dto = new AnalyticsSnapshotRequestDto(
                UUID.randomUUID().toString(),
                "2026-04-21T18:00:00Z",
                "war",
                List.of(
                        new AnalyticsSnapshotRequestDto.PlayerSnapshotDto(
                                "char1", "clan1", 100, 50, 1000,
                                List.of(), List.of(), false
                        )
                ),
                List.of()
        );

        when(analyticsService.saveSnapshot(any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/internal/analytics/snapshots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isAccepted());

        verify(analyticsService).saveSnapshot(any());
    }

    @Test
    void publishSnapshot_givenInvalidDto_shouldReturn400() throws Exception {
        // Missing gameId
        AnalyticsSnapshotRequestDto dto = new AnalyticsSnapshotRequestDto(
                "",
                "2026-04-21T18:00:00Z",
                "war",
                List.of(
                        new AnalyticsSnapshotRequestDto.PlayerSnapshotDto(
                                "char1", "clan1", 100, 50, 1000,
                                List.of(), List.of(), false
                        )
                ),
                List.of()
        );

        mockMvc.perform(post("/internal/analytics/snapshots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(analyticsService);
    }
}
