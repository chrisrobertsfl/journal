package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.*;
import com.chrisrobertsfl.journal.task.service.TaskService;
import com.google.common.base.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;
import static java.util.Objects.isNull;

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
            return ResponseEntity.status(201).body(TaskResponse.success(taskService.addTask(task)));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(TaskResponse.error(e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskInfo task) {
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.updateTask(task)));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(404).body(TaskResponse.error(e.getMessage()));
        } catch (MissingTaskException e) {
            return ResponseEntity.status(400).body(TaskResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(TaskResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@RequestBody String id) {
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.deleteTask(id)));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable String id) {
        try {
            Optional<TaskInfo> found = taskService.findById(id);
            return ResponseEntity.ok(TaskResponse.success(found.get()));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/label")
    public ResponseEntity<TaskListResponse> findByLabel(@RequestParam Set<String> labels) {
        if (isNull(labels) || labels.isEmpty()) {
            return ResponseEntity.badRequest().body(TaskListResponse.error("No labels provided"));
        }
        List<TaskInfo> byLabel = taskService.findByLabel(labels);
        return byLabel.isEmpty()
                ? ResponseEntity.status(404).body(TaskListResponse.error(format("No tasks found for label(s):  %s", new TreeSet<>(labels))))
                : ResponseEntity.ok(TaskListResponse.success(byLabel));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<TaskListResponse> findByStatus(@PathVariable String status) {
        if (Strings.isNullOrEmpty(status)) {
            return ResponseEntity.badRequest().body(TaskListResponse.error("No status provided"));
        }
        List<TaskInfo> byStatus = taskService.findByStatus(status);
        return byStatus.isEmpty()
                ? ResponseEntity.status(404).body(TaskListResponse.error(format("No tasks found with status: %s", status)))
                : ResponseEntity.ok(TaskListResponse.success(byStatus));
    }

    @PutMapping("/{id}/mark-in-progress")
    public ResponseEntity<TaskResponse> markInProgress(@PathVariable String id) {
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.markInProgress(id)));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(404).body(TaskResponse.error(e.getMessage()));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(TaskResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markComplete(@PathVariable String id) {
        try {
            return ResponseEntity.ok(TaskResponse.success(taskService.markComplete(id)));
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(404).body(TaskResponse.error(e.getMessage()));
        } catch (TaskException e) {
            return ResponseEntity.badRequest().body(TaskResponse.error(e.getMessage()));
        }
    }
}
