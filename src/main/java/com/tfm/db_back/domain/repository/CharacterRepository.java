package com.tfm.db_back.domain.repository;

import com.tfm.db_back.domain.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {
    
    List<Character> findByUserId(UUID userId);
    
    boolean existsByName(String name);
}
