package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad User.
 * Spring Data genera automáticamente las queries derivadas del nombre del método.
 * NUNCA se llama directamente desde un controlador — siempre a través de UserService.
 *
 * @author Adrián González Blanco
 * @author Adriana Cabaleiro Álvarez
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    // Búsqueda por username para el flujo de login del Middle Server
    Optional<User> findByUsername(String username);

    // Validación de unicidad antes de crear un usuario (evita race condition con try/catch)
    boolean existsByUsername(String username);

    // Validación de unicidad de email antes de crear un usuario
    boolean existsByEmail(String email);

    // Conteo de usuarios baneados para estadísticas de administrador
    long countByIsBannedTrue();

    // Listado de usuarios ordenados por fecha de creación (más recientes primero)
    java.util.List<User> findAllByOrderByCreatedAtDesc();

    // Obtener los usuarios clasificados en el podio (Top 3 puntuaciones distintas, orden descendente)
    @org.springframework.data.jpa.repository.Query(value = 
        "SELECT * FROM users WHERE gloria_eterna > 0 AND gloria_eterna IN " +
        "(SELECT DISTINCT gloria_eterna FROM users WHERE gloria_eterna > 0 ORDER BY gloria_eterna DESC LIMIT 3) " +
        "ORDER BY gloria_eterna DESC", nativeQuery = true)
    java.util.List<User> findTop3Ranking();
}
