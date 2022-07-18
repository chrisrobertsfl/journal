package com.sgintl.journal;

import io.quarkus.mongodb.panache.PanacheMongoRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JournalRepository implements PanacheMongoRepository<JournalEntity> {
}
