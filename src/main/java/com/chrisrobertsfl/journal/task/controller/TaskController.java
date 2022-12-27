package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.TaskException;
import com.chrisrobertsfl.journal.task.model.TaskInfo;
import com.chrisrobertsfl.journal.task.model.TaskResponse;
import com.chrisrobertsfl.journal.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskInfo>> findAll() {
        List<TaskInfo> tasks = taskService.findAll();
        return new ResponseEntity<>(tasks, OK);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskInfo taskInfo) {
        return null;

    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskInfo> updateTask(@PathVariable String id, @RequestBody TaskInfo taskInfo) {
        taskService.updateTask(taskInfo);
        return taskService.findById(taskInfo.id())
                .map(t -> ResponseEntity.status(OK).body(t))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskInfo> findById(@PathVariable String id) {
        Optional<TaskInfo> task = taskService.findById(id);
        return task.map(value -> new ResponseEntity<>(value, OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/label/{labels}")
    public ResponseEntity<List<TaskInfo>> findByLabel(@PathVariable Set<String> labels) {
        List<TaskInfo> tasks = taskService.findByLabel(labels);
        return new ResponseEntity<>(tasks, OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskInfo>> findByStatus(@PathVariable String status) {
        List<TaskInfo> tasks = taskService.findByStatus(status);
        return new ResponseEntity<>(tasks, OK);
    }

    @PutMapping("/markInProgress/{id}")
    public ResponseEntity<Void> markInProgress(@PathVariable String id) {
        taskService.markInProgress(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/markInProgress/{id}")
    public ResponseEntity<Void> markComplete(@PathVariable String id) {
        taskService.markComplete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
