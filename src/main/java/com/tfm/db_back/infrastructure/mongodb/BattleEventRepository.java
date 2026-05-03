package com.tfm.db_back.infrastructure.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de MongoDB para la entidad BattleEventDocument.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Repository
public interface BattleEventRepository extends MongoRepository<BattleEventDocument, String> {
}
