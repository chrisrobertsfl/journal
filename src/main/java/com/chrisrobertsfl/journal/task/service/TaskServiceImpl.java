package com.chrisrobertsfl.journal.task.service;

import com.chrisrobertsfl.journal.task.model.MissingTaskException;
import com.chrisrobertsfl.journal.task.model.Task;
import com.chrisrobertsfl.journal.task.model.TaskAggregateRoot;
import com.chrisrobertsfl.journal.task.model.TaskInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.chrisrobertsfl.journal.task.model.Status.valueOf;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;


@Service
public class TaskServiceImpl implements TaskService {

    @Resource(name = "taskAggregateRoot")
    TaskAggregateRoot taskAggregateRoot;

    public TaskServiceImpl(TaskAggregateRoot taskAggregateRoot) {
        this.taskAggregateRoot = taskAggregateRoot;
    }

    @Override
    public List<TaskInfo> findAll() {
        return taskAggregateRoot.findAll().stream()
                .map(TaskInfo::fromTask)
                .collect(toList());
    }

    @Override
    public TaskInfo addTask(TaskInfo taskInfo) {
        Task task = ofNullable(taskInfo)
                .map(TaskInfo::toTask)
                .orElseThrow(() -> new MissingTaskException("Task ID cannot be null"));
        return taskAggregateRoot.addTask(task).toTaskInfo();
    }

    @Override
    public TaskInfo updateTask(TaskInfo task) {
        return taskAggregateRoot.updateTask(task.toTask()).toTaskInfo();
    }

    @Override
    public TaskInfo deleteTask(String id) {
        return taskAggregateRoot.deleteTask(ofNullable(id)
                        .orElseThrow(() -> new MissingTaskException("Task ID cannot be null")))
                .toTaskInfo();
    }

    @Override
    public Optional<TaskInfo> findById(String id) {
        return taskAggregateRoot.findById(id)
                .map(TaskInfo::fromTask);
    }

    @Override
    public List<TaskInfo> findByLabel(Set<String> labels) {
        return taskAggregateRoot.findByLabel(labels).stream()
                .map(TaskInfo::fromTask)
                .collect(toList());
    }

    @Override
    public List<TaskInfo> findByStatus(String status) {
        return taskAggregateRoot.findByStatus(valueOf(status)).stream()
                .map(TaskInfo::fromTask)
                .collect(toList());
    }

    @Override
    public TaskInfo markInProgress(String id) {
        return taskAggregateRoot.markInProgress(id)
                .toTaskInfo();
    }

    @Override
    public TaskInfo markComplete(String id) {
        return taskAggregateRoot.markComplete(id)
                .toTaskInfo();
    }
}