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
 * @author Adrián González Blando
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    // Búsqueda por username para el flujo de login del Middle Server
    Optional<User> findByUsername(String username);

    // Validación de unicidad antes de crear un usuario (evita race condition con try/catch)
    boolean existsByUsername(String username);

    // Validación de unicidad de email antes de crear un usuario
    boolean existsByEmail(String email);
}
