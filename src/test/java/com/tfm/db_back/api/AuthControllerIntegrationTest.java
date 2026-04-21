package com.tfm.db_back.api;

import com.tfm.db_back.AbstractIntegrationTest;
import com.tfm.db_back.api.dto.HandshakeRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${DB_HANDSHAKE_SECRET}")
    private String correctSecret;

    @Test
    void handshake_givenCorrectSecret_shouldReturn200AndToken() {
        HandshakeRequestDto request = new HandshakeRequestDto(correctSecret);
        String url = "http://localhost:" + port + "/internal/auth/handshake";
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"token\"");
    }

    @Test
    void handshake_givenWrongSecret_shouldReturn401() {
        HandshakeRequestDto request = new HandshakeRequestDto("wrong-secret");
        String url = "http://localhost:" + port + "/internal/auth/handshake";
        
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
