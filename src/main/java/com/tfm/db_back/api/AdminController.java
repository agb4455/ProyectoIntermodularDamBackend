package com.tfm.db_back.api;

import com.tfm.db_back.api.dto.AdminStatsResponseDto;
import com.tfm.db_back.api.dto.ApiResponse;
import com.tfm.db_back.api.dto.UserResponseDto;
import com.tfm.db_back.domain.service.GameService;
import com.tfm.db_back.domain.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para operaciones de administración interna.
 * Protegido por HandshakeJwtFilter: solo el Middle Server puede llamar estos endpoints.
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
@RestController
@RequestMapping("/internal/admin")
public class AdminController {

    private final UserService userService;
    private final GameService gameService;

    public AdminController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    /**
     * GET /internal/admin/stats
     * Devuelve las estadísticas globales del sistema.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponseDto>> getGlobalStats() {
        AdminStatsResponseDto stats = new AdminStatsResponseDto(
                userService.getTotalUsers(),
                gameService.getTotalGamesCount(),
                userService.getBannedUsersCount()
        );
        return ResponseEntity.ok(new ApiResponse<>(stats));
    }

    /**
     * GET /internal/admin/users
     * Devuelve el listado de todos los usuarios del sistema.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(users));
    }

    /**
     * PUT /internal/admin/users/{id}/ban
     * Banea a un usuario.
     */
    @PutMapping("/users/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        userService.banUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /internal/admin/users/{id}/unban
     * Desbanea a un usuario.
     */
    @PutMapping("/users/{id}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID id) {
        userService.unbanUser(id);
        return ResponseEntity.noContent().build();
    }
}
