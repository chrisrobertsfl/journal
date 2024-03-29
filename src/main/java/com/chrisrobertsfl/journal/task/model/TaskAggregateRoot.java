package com.chrisrobertsfl.journal.task.model;

import com.chrisrobertsfl.journal.task.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.chrisrobertsfl.journal.task.model.Status.COMPLETED;
import static com.chrisrobertsfl.journal.task.model.Status.IN_PROGRESS;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Component
public class TaskAggregateRoot {
    private final TaskRepository taskRepository;

    public TaskAggregateRoot(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task addTask(Task task) {
        return taskRepository.save(ofNullable(task)
                .orElseThrow(() -> new MissingTaskException("Need task present to add")));
    }

    public Task updateTask(Task task) {
        ofNullable(task)
                .orElseThrow(() -> new MissingTaskException("Task is missing"));
        findById(task.id())
                .orElseThrow(() -> new TaskNotFoundException(format("No Task found for id %s", task.id())));
        return taskRepository.save(task);
    }

    public Task deleteTask(String id) {
        Task deleted = findById(id)
                .orElseThrow(() -> new TaskNotFoundException(format("No Task found for id %s", id)));
        taskRepository.deleteById(deleted.id());
        return deleted;
    }

    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> findByLabel(Set<String> labels) {
        return taskRepository.findByLabelsIn(labels);
    }

    public List<Task> findByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    public Task markInProgress(String id) {
        return taskRepository
                .findById(id)
                .map(task -> taskRepository.save(new Task(task.id(), task.name(), task.description(), task.createdAt(), task.priority(), IN_PROGRESS, task.labels(), task.subtasks())))
                .orElseThrow(() -> new TaskNotFoundException(format("Task with ID '%s' not found", id)));
    }

    public Task markComplete(String id) {
        return taskRepository
                .findById(id)
                .map(task -> taskRepository.save(new Task(task.id(), task.name(), task.description(), task.createdAt(), task.priority(), COMPLETED, task.labels(), task.subtasks())))
                .orElseThrow(() -> new TaskNotFoundException(format("Task with ID '%s' not found", id)));
    }
}

