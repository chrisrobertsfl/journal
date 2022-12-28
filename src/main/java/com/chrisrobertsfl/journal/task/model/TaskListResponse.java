package com.chrisrobertsfl.journal.task.model;

import java.util.List;

public record TaskListResponse(List<TaskInfo> tasks, String error) {
    public static TaskListResponse success(List<TaskInfo> tasks) {
        return new TaskListResponse(tasks, null);
    }

    public static TaskListResponse error(String error) {
        return new TaskListResponse(null, error);
    }
}
