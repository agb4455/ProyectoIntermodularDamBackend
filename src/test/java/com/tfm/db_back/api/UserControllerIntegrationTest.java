package com.tfm.db_back.api;

import com.tfm.db_back.AbstractIntegrationTest;
import com.tfm.db_back.api.dto.CreateUserRequestDto;
import com.tfm.db_back.api.dto.HandshakeRequestDto;
import com.tfm.db_back.api.dto.UpdateAvatarRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

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
    void userCrudFlow_shouldCreateReadUpdateAndHandleConflicts() {
        String baseUrl = "http://localhost:" + port + "/internal/users";

        // 1. Create User
        CreateUserRequestDto createDto = new CreateUserRequestDto("ragnarr", "ragnarr@kattegat.com", "Odin1234");
        HttpEntity<CreateUserRequestDto> request = new HttpEntity<>(createDto, authHeaders);
        ResponseEntity<java.util.Map> createRes = restTemplate.postForEntity(baseUrl, request, java.util.Map.class);
        
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) createRes.getBody().get("data");
        String userIdStr = (String) data.get("id");

        // 2. Read by ID
        HttpEntity<Void> getRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<java.util.Map> getRes = restTemplate.exchange(baseUrl + "/" + userIdStr, HttpMethod.GET, getRequest, java.util.Map.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Update Avatar
        UpdateAvatarRequestDto avatarDto = new UpdateAvatarRequestDto("https://minio.cloud/avatar.png");
        HttpEntity<UpdateAvatarRequestDto> putRequest = new HttpEntity<>(avatarDto, authHeaders);
        ResponseEntity<java.util.Map> putRes = restTemplate.exchange(baseUrl + "/" + userIdStr + "/avatar", HttpMethod.PUT, putRequest, java.util.Map.class);
        assertThat(putRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 4. Conflict on Username
        CreateUserRequestDto conflictDto = new CreateUserRequestDto("ragnarr", "otherrr@mail.com", "Password123");
        HttpEntity<CreateUserRequestDto> conflictReq = new HttpEntity<>(conflictDto, authHeaders);
        try {
            restTemplate.postForEntity(baseUrl, conflictReq, java.util.Map.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }
}
