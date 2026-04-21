package com.tfm.db_back.api;

import com.tfm.db_back.AbstractIntegrationTest;
import com.tfm.db_back.api.dto.CreateCharacterRequestDto;
import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.HandshakeRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CharacterControllerIntegrationTest extends AbstractIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${DB_HANDSHAKE_SECRET}")
    private String correctSecret;

    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        String url = "http://localhost:" + port + "/internal/auth/handshake";
        HandshakeRequestDto request = new HandshakeRequestDto(correctSecret);
        ResponseEntity<java.util.Map> response = restTemplate.postForEntity(url, request, java.util.Map.class);
        String token = (String) ((java.util.Map<String, Object>) response.getBody().get("data")).get("token");
        
        authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
    }

    @Test
    void characterFlow_shouldCreateAndRetrieveCharacter() {
        // 1. Create User
        String uUrl = "http://localhost:" + port + "/internal/users";
        CreateUserRequestDto userDto = new CreateUserRequestDto("bj" + UUID.randomUUID().toString().substring(0, 5), "bj_" + UUID.randomUUID().toString() + "@kat.com", "Password123");
        HttpEntity<CreateUserRequestDto> userReq = new HttpEntity<>(userDto, authHeaders);
        ResponseEntity<java.util.Map> userRes = restTemplate.postForEntity(uUrl, userReq, java.util.Map.class);
        String userId = (String) ((java.util.Map<String, Object>) userRes.getBody().get("data")).get("id");

        // 2. Create Character
        String cUrl = "http://localhost:" + port + "/internal/characters";
        CreateCharacterRequestDto charDto = new CreateCharacterRequestDto(UUID.fromString(userId), "berserkers", "Bjorn Ironside");
        HttpEntity<CreateCharacterRequestDto> charReq = new HttpEntity<>(charDto, authHeaders);
        ResponseEntity<java.util.Map> charRes = restTemplate.postForEntity(cUrl, charReq, java.util.Map.class);
        
        assertThat(charRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String characterId = (String) ((java.util.Map<String, Object>) charRes.getBody().get("data")).get("id");

        // 3. Get Character
        HttpEntity<Void> getReq = new HttpEntity<>(authHeaders);
        ResponseEntity<java.util.Map> getRes = restTemplate.exchange(cUrl + "/" + characterId, HttpMethod.GET, getReq, java.util.Map.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((java.util.Map<String, Object>) getRes.getBody().get("data")).get("clanId")).isEqualTo("berserkers");
    }
}
