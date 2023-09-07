package com.konkuk.gp.domain.dao.dialog;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DialogHistoryRepository extends MongoRepository<DialogHistory,String> {
}
