package com.chrisrobertsfl.journal.repository;

import com.chrisrobertsfl.journal.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {

}

