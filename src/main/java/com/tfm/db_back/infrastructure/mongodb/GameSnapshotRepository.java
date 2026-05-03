package com.tfm.db_back.infrastructure.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de MongoDB para la entidad GameSnapshotDocument.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Repository
public interface GameSnapshotRepository extends MongoRepository<GameSnapshotDocument, String> {
}
