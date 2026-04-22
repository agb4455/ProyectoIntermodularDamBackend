package com.tfm.db_back.api;

import com.tfm.db_back.AbstractIntegrationTest;
import com.tfm.db_back.api.dto.AnalyticsSnapshotRequestDto;
import com.tfm.db_back.api.dto.HandshakeRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AnalyticsControllerIntegrationTest extends AbstractIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${DB_HANDSHAKE_SECRET}")
    private String correctSecret;

    private HttpHeaders authHeaders;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        String url = baseUrl + "/internal/auth/handshake";
        HandshakeRequestDto request = new HandshakeRequestDto(correctSecret);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        String token = (String) ((Map<String, Object>) response.getBody().get("data")).get("token");
        
        authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
    }

    @Test
    void saveSnapshot_shouldReturn202Accepted() {
        AnalyticsSnapshotRequestDto.PlayerSnapshotDto pDto = new AnalyticsSnapshotRequestDto.PlayerSnapshotDto(
            UUID.randomUUID().toString(),
            "jarls",
            100, 50, 1000,
            Collections.emptyList(),
            Collections.emptyList(),
            false
        );

        AnalyticsSnapshotRequestDto dto = new AnalyticsSnapshotRequestDto(
            UUID.randomUUID().toString(),
            "2026-04-21T00:00:00Z",
            "war",
            List.of(pDto),
            List.of()
        );

        HttpEntity<AnalyticsSnapshotRequestDto> req = new HttpEntity<>(dto, authHeaders);
        ResponseEntity<Void> res = restTemplate.postForEntity(baseUrl + "/internal/analytics/snapshots", req, Void.class);
        
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}
