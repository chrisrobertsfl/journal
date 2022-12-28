package com.chrisrobertsfl.journal.task.model;

public record TaskResponse(TaskInfo task, String error) {
    public static TaskResponse success(TaskInfo task) {
        return new TaskResponse(task, null);
    }

    public static TaskResponse error(String error) {
        return new TaskResponse(null, error);
    }
}
