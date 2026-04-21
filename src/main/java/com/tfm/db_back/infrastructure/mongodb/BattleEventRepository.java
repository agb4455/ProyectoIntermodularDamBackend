package com.tfm.db_back.infrastructure.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleEventRepository extends MongoRepository<BattleEventDocument, String> {
}
