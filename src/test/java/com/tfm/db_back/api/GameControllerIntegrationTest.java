package com.tfm.db_back.api;

import com.tfm.db_back.AbstractIntegrationTest;
import com.tfm.db_back.api.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GameControllerIntegrationTest extends AbstractIntegrationTest {

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
    void gameFlow_shouldCreateDumpAndEnd() {
        // 1. Create 2 Users & Characters
        String u1 = createUser("g1_user1");
        String c1 = createCharacter(u1, "valkirias");
        String u2 = createUser("g1_user2");
        String c2 = createCharacter(u2, "jarls");

        // 2. Create Game
        CreateGameRequestDto gameDto = new CreateGameRequestDto((short) 2, List.of(UUID.fromString(c1), UUID.fromString(c2)));
        HttpEntity<CreateGameRequestDto> gameReq = new HttpEntity<>(gameDto, authHeaders);
        ResponseEntity<Map> gameRes = restTemplate.postForEntity(baseUrl + "/internal/games", gameReq, Map.class);
        
        assertThat(gameRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String gameId = (String) ((Map<String, Object>) gameRes.getBody().get("data")).get("id");

        // 3. Dump State
        StateDumpRequestDto dumpDto = new StateDumpRequestDto("{\"status\":\"active\"}");
        HttpEntity<StateDumpRequestDto> dumpReq = new HttpEntity<>(dumpDto, authHeaders);
        ResponseEntity<Void> putRes = restTemplate.exchange(baseUrl + "/internal/games/" + gameId + "/state", HttpMethod.PUT, dumpReq, Void.class);
        assertThat(putRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 4. Get Active Games
        HttpEntity<Void> getReq = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> activeRes = restTemplate.exchange(baseUrl + "/internal/games/active", HttpMethod.GET, getReq, Map.class);
        assertThat(activeRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 5. End Game
        EndGameRequestDto endDto = new EndGameRequestDto(UUID.fromString(c1));
        HttpEntity<EndGameRequestDto> endReq = new HttpEntity<>(endDto, authHeaders);
        ResponseEntity<Map> endRes = restTemplate.postForEntity(baseUrl + "/internal/games/" + gameId + "/end", endReq, Map.class);
        assertThat(endRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String createUser(String prefix) {
        String uid = UUID.randomUUID().toString().substring(0, 5);
        CreateUserRequestDto userDto = new CreateUserRequestDto(prefix + "_" + uid, prefix + "_" + uid + "@test.com", "Pass1234");
        ResponseEntity<Map> userRes = restTemplate.postForEntity(baseUrl + "/internal/users", new HttpEntity<>(userDto, authHeaders), Map.class);
        return (String) ((Map<String, Object>) userRes.getBody().get("data")).get("id");
    }

    private String createCharacter(String userId, String clanId) {
        CreateCharacterRequestDto charDto = new CreateCharacterRequestDto(UUID.fromString(userId), clanId, "Char " + UUID.randomUUID().toString().substring(0, 5));
        ResponseEntity<Map> charRes = restTemplate.postForEntity(baseUrl + "/internal/characters", new HttpEntity<>(charDto, authHeaders), Map.class);
        return (String) ((Map<String, Object>) charRes.getBody().get("data")).get("id");
    }
}
