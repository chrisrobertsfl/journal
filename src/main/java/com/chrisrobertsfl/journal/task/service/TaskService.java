package com.chrisrobertsfl.journal.task.service;

import com.chrisrobertsfl.journal.task.model.TaskInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskService {
    List<TaskInfo> findAll();
    TaskInfo addTask(TaskInfo task);
    TaskInfo updateTask(TaskInfo task);
    TaskInfo deleteTask(String id);
    Optional<TaskInfo> findById(String id);
    List<TaskInfo> findByLabel(Set<String> labels);
    List<TaskInfo> findByStatus(String status);
    TaskInfo markInProgress(String id);
    TaskInfo markComplete(String id);
}

