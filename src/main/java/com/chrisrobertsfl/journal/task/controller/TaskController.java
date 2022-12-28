package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.TaskInfo;
import com.chrisrobertsfl.journal.task.model.TaskResponse;
import com.chrisrobertsfl.journal.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskInfo task) {
        return Optional.of(task)
                .map(taskService::addTask)
                .map(TaskResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(TaskResponse.error("Error adding task")));
    }

}
