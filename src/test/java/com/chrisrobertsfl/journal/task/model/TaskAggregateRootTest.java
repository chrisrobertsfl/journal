package com.chrisrobertsfl.journal.task.model;

import com.chrisrobertsfl.journal.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.chrisrobertsfl.journal.task.model.Priority.HIGH;
import static com.chrisrobertsfl.journal.task.model.Priority.LOW;
import static com.chrisrobertsfl.journal.task.model.Status.*;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Given Task Aggregate Root")
public class TaskAggregateRootTest {
    @Mock
    TaskRepository taskRepository;
    TaskAggregateRoot taskAggregateRoot;


    @Test
    @DisplayName("should return an empty list when no tasks are found")
    public void testFindAll_empty() {
        when(taskRepository.findAll()).thenReturn(emptyList());
        taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        List<Task> result = taskAggregateRoot.findAll();

        assertAll(
                () -> assertEquals(emptyList(), result, "Incorrect tasks")
        );
        verify(taskRepository).findAll();
    }

    @Nested
    @DisplayName("when finding all tasks")
    class FindAll {
        Task task1;
        Task task2;

        @BeforeEach
        public void setUp() {
            task1 = new Task("1", "Task 1", "Description 1", Instant.now(), HIGH, PENDING, null, null);
            task2 = new Task("2", "Task 2", "Description 2", Instant.now(), LOW, IN_PROGRESS, null, null);
            when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("should return all tasks")
        public void testFindAll() {
            List<Task> tasks = taskAggregateRoot.findAll();
            assertAll(
                    () -> assertEquals(2, tasks.size(), "Incorrect number of tasks"),
                    () -> assertEquals(task1, tasks.get(0), "Incorrect task at index 0"),
                    () -> assertEquals(task2, tasks.get(1), "Incorrect task at index 1")
            );
            verify(taskRepository).findAll();
        }
    }

    @Nested
    @DisplayName("when finding a task by id")
    class FindById {
        Task task;

        @BeforeEach
        public void setUp() {
            task = new Task("1", "Task 1", "Description 1", Instant.now(), HIGH, PENDING, null, null);
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("should return the task if it exists")
        public void testFindById_exists() {
            when(taskRepository.findById("1")).thenReturn(Optional.of(task));
            Optional<Task> optionalTask = taskAggregateRoot.findById("1");
            assertAll(
                    () -> assertTrue(optionalTask.isPresent(), "Task should be present"),
                    () -> assertEquals(task, optionalTask.get(), "Incorrect task")
            );
            verify(taskRepository).findById("1");
        }

        @Test
        @DisplayName("should return an empty optional if the task does not exist")
        public void testFindById_notExists() {
            when(taskRepository.findById("2")).thenReturn(empty());
            Optional<Task> optionalTask = taskAggregateRoot.findById("2");
            assertFalse(optionalTask.isPresent(), "Task should not be present");
            verify(taskRepository).findById("2");
        }
    }

    @Nested
    @DisplayName("when adding a task")
    class AddTask {

        Task task;

        @BeforeEach
        public void setUp() {
            task = new Task("1", "Task 1", "Description", Instant.now(), HIGH, PENDING, null, null);
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("should add the task")
        public void testAddTask() {
            taskAggregateRoot.addTask(task);
            verify(taskRepository).save(task);
        }

        @Test
        @DisplayName("throws exception when task is null")
        void throwsExceptionWhenTaskIsNull() { //TODO
            assertThrows(MissingTaskException.class, () -> taskAggregateRoot.addTask(null), "Expected MissingTaskException to be thrown when adding a null task");
        }
    }

    @Nested
    @DisplayName("when updating a task")
    class UpdateTask {
        final String taskId = "1";
        Task task;

        @BeforeEach
        void setUp() {
            task = new Task(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), emptyList());
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("should throw an MissingTaskException when the task is null")
        public void testUpdateTask_nullTask() {
            assertThrows(MissingTaskException.class, () -> taskAggregateRoot.updateTask(null), "Task must be present when updating it");
        }

        @Test
        @DisplayName("throws exception when task not found")
        void throwsExceptionWhenTaskNotFound() {
            when(taskRepository.findById(taskId)).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.updateTask(task), format("Task with id '%s' not found", taskId));
        }

        @Test
        @DisplayName("should update the task in the repository")
        public void testUpdateTask() {
            when(taskRepository.findById("1")).thenReturn(Optional.of(task));
            when(taskRepository.save(task)).thenReturn(task);
            taskAggregateRoot.updateTask(task);
            verify(taskRepository).save(task);
        }
    }

    @Nested
    @DisplayName("when marking in progress")
    class MarkInProgress {
        final String taskId = "1";
        Task task;

        @BeforeEach
        void setUp() {
            task = new Task(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), emptyList());
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("throws exception when task not found")
        void throwsExceptionWhenTaskNotFound() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markInProgress("2"), format("Task with id %s not found", "2"));
        }

        @Test
        @DisplayName("updates status to IN_PROGRESS")
        void updatesStatusToInProgress() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            Task updated = new Task(taskId, "Task 1", "Description", Instant.now(), HIGH, IN_PROGRESS, Set.of("label1"), emptyList());
            when(taskRepository.save(any(Task.class))).thenReturn(updated);
            taskAggregateRoot.markInProgress(taskId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("does not update status of different task")
        void doesNotUpdateStatusOfDifferentTask() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markInProgress("2"), format("Task with id %s not found", "2"));
        }
    }

    @Nested
    @DisplayName("when marking complete")
    class MarkComplete {
        final String taskId = "1";
        Task task;

        @BeforeEach
        void setUp() {
            task = new Task(taskId, "Task 1", "Description", Instant.now(), HIGH, IN_PROGRESS, Set.of("label1"), emptyList());
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("throws exception when task not found")
        void throwsExceptionWhenTaskNotFound() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markComplete("2"), format("Task with id %s not found", "2"));
        }

        @Test
        @DisplayName("updates status to COMPLETED")
        void updatesStatusToCompleted() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            Task updated = new Task(taskId, "Task 1", "Description", Instant.now(), HIGH, COMPLETED, Set.of("label1"), emptyList());
            when(taskRepository.save(any(Task.class))).thenReturn(updated);
            taskAggregateRoot.markComplete(taskId);
            verify(taskRepository).save(any());
        }

        @Test
        @DisplayName("does not update status of different task")
        void doesNotUpdateStatusOfDifferentTask() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markComplete("2"), "Expected TaskNotFoundException to be thrown when marking a task as complete but task was not found");
        }
    }

}