package com.chrisrobertsfl.journal.task.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public record TaskInfo(String id, String name, String description, Instant createdAt, Priority priority,
                       Status status, Set<String> labels, List<TaskInfo> subtasks) {

    public static TaskInfo nullTaskInfo() {
        return new TaskInfo(null, null, null, null, null, null, null, null);
    }

    public static TaskInfo fromTask(Task task) {
        List<TaskInfo> subTasks = ofNullable(task.subtasks())
                .map(t -> t.stream()
                        .map(TaskInfo::fromTask)
                        .collect(toList()))
                .orElse(emptyList());
        return new TaskInfo(task.id(), task.name(), task.description(), task.createdAt(), task.priority(),
                task.status(), task.labels(), subTasks);
    }

    public Task toTask() {
        List<Task> subTasks = ofNullable(subtasks)
                .map(t -> t.stream()
                        .map(TaskInfo::toTask)
                        .collect(toList()))
                .orElse(emptyList());
        return new Task(id, name, description, createdAt, priority, status, labels, subTasks);
    }
}
