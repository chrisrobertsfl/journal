package com.sgintl.journal;

import lombok.Data;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@SuppressWarnings("AccessStaticViaInstance")
@ApplicationScoped
@Data
public class JournalService {
    static Long counter = 0L;
    @Inject
    JournalRepository journalRepository;

    public Journal saveJournal(final Journal journal) {
        JournalEntity journalEntity = JournalEntity.builder()
                .description(journal.getDescription())
                .build();
        journalRepository.persist(journalEntity);
        Journal created = Journal.builder()
                .id(journalEntity.getId().toString())
                .description(journalEntity.getDescription())
                .build();
        return created;
    }

    public Journal findJournalById(final String id) {
        Optional<JournalEntity> result = journalRepository.findByIdOptional(new ObjectId(id));
        return result.map(j -> Journal.builder()
                        .id(j.getId().toString())
                        .description(j.getDescription())
                        .build())
                .orElse(Journal.EMPTY);
    }

    public Journal updateJournal(Journal journal) {
        Optional<JournalEntity> optionalResult = journalRepository.findByIdOptional(new ObjectId(journal.getId()));
        if (optionalResult.isEmpty()) {
            return Journal.EMPTY;
        }
        JournalEntity result = optionalResult.get();
        result.setDescription(journal.getDescription());
        journalRepository.update(result);
        return Journal.builder()
                .id(result.getId().toString())
                .description(result.getDescription())
                .build();
    }

    public Journal deleteJournal(String id) {
        Optional<JournalEntity> optionalResult = journalRepository.findByIdOptional(new ObjectId(id));
        if (optionalResult.isEmpty()) {
            return Journal.EMPTY;
        }
        boolean success = journalRepository.deleteById(new ObjectId(id));
        if (!success) {
            return Journal.EMPTY;
        }
        JournalEntity result = optionalResult.get();
        return Journal.builder()
                .id(result.getId().toString())
                .description(result.getDescription())
                .build();
    }
}
