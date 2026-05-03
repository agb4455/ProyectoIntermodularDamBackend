package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Character.
 * Permite gestionar la persistencia de los personajes de los jugadores.
 *
 * @author Adrián González Blando
 */
@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {
    
    List<Character> findByUserId(UUID userId);
    
    boolean existsByName(String name);
}
