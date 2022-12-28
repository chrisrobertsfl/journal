package com.chrisrobertsfl.journal.task.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public record Task(String id, String name, String description, Instant createdAt, Priority priority,
                   Status status, Set<String> labels, List<Task> subtasks) {
    public static Task nullTask() {
        return new Task(null, null, null, null, null, null, null, null);
    }

    public TaskInfo toTaskInfo() {
        List<TaskInfo> subTasks = ofNullable(subtasks)
                .map(t -> t.stream()
                        .map(Task::toTaskInfo)
                        .collect(toList()))
                .orElse(emptyList());
        return new TaskInfo(id, name, description, createdAt, priority, status, labels, subTasks);
    }
}
