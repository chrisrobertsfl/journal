package com.chrisrobertsfl.journal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "tasks")
public record Task(@Id String id, String name, String description, State state, Set<String> labels) implements Item {

    public Task(final TaskInfo taskInfo) {
        this(taskInfo.id(), taskInfo.name(), taskInfo.description(), taskInfo.state(), taskInfo.labels());
    }

    @Override
    public boolean exists(String label) {
        return labels.stream().anyMatch(l -> l.equalsIgnoreCase(label));
    }
}
