package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.*;
import com.chrisrobertsfl.journal.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.addTask(task)));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(TaskResponse.error(e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskInfo task) {
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.updateTask(task)));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(TaskResponse.error(e.getMessage()));
        }
    }

    public ResponseEntity<TaskResponse> deleteTask(@RequestBody String id) {
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.deleteTask(id)));
        } catch(TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        }
    }

    public ResponseEntity<TaskResponse> findById(String id) {
        try {
            Optional<TaskInfo> found = taskService.findById(id);
            return ResponseEntity.ok(TaskResponse.success(found.get()));
        } catch(TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        }
    }
}
