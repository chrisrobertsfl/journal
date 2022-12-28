package com.chrisrobertsfl.journal.controller;

import com.chrisrobertsfl.journal.model.TaskInfo;
import com.chrisrobertsfl.journal.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Resource(name = "taskService")
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskInfo> createTask(@RequestBody TaskInfo taskInfo) {
        TaskInfo created = taskService.create(taskInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}