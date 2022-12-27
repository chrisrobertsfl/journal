package com.chrisrobertsfl.journal.task.model;

public record TaskResponse(String successMessage, String errorMessage, TaskInfo body) {
    public static TaskResponse body(TaskInfo body) {
        return new TaskResponse(null, null, body);
    }

    public static TaskResponse success(String successMessage) {
        return new TaskResponse(successMessage, null, null);
    }
    public static TaskResponse error(String errorMessage) {
        return new TaskResponse(null, errorMessage, null);
    }
}
