package com.chrisrobertsfl.journal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

@Document(collection = "tasks")
public record TaskInfo(@Id String id, String name, String description, State state, Set<String> labels) implements Serializable {
    public TaskInfo(String id, String name, String description, State state) {
        this(id, name, description, state, null);
    }

    public TaskInfo(final Task task) {
        this(task.id(), task.name(), task.description(), task.state(), task.labels());
    }
}
