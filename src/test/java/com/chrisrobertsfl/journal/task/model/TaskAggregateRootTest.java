package com.chrisrobertsfl.journal.task.model;

import com.chrisrobertsfl.journal.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    @InjectMocks
    TaskAggregateRoot taskAggregateRoot;


    @Test
    @DisplayName("should return an empty list when no tasks are found")
    public void testFindAll_empty() {
        when(taskRepository.findAll()).thenReturn(emptyList());
        assertEquals(emptyList(), taskAggregateRoot.findAll(), "Incorrect tasks");
        verify(taskRepository).findAll();
    }

    @Nested
    @DisplayName("when finding all tasks")
    class FindAll {
        List<Task> tasks = List.of(
                new Task("1", "Task 1", "Description 1", Instant.now(), HIGH, PENDING, null, null),
                new Task("2", "Task 2", "Description 2", Instant.now(), LOW, IN_PROGRESS, null, null)
        );

        @BeforeEach
        public void setUp() {
            when(taskRepository.findAll()).thenReturn(tasks);
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("should return all tasks")
        public void testFindAll() {
            List<Task> found = taskAggregateRoot.findAll();
            assertAll(
                    () -> assertEquals(2, found.size(), "Incorrect number of tasks"),
                    () -> assertEquals(found.get(0), found.get(0), "Incorrect task at index 0"),
                    () -> assertEquals(found.get(1), found.get(1), "Incorrect task at index 1")
            );
            verify(taskRepository).findAll();
        }
    }

    @Nested
    @DisplayName("when finding a task by id")
    class FindById {

        @BeforeEach
        public void setUp() {
            taskAggregateRoot = new TaskAggregateRoot(taskRepository);
        }

        @Test
        @DisplayName("should return the task if it exists")
        public void testFindById_exists() {
            Task task = new Task("1", "Task 1", "Description 1", Instant.now(), HIGH, PENDING, null, null);
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
            assertFalse(taskAggregateRoot.findById("2").isPresent(), "Task should not be present");
            verify(taskRepository).findById("2");
        }
    }

    @Nested
    @DisplayName("when adding a task")
    class AddTask {

        @Test
        @DisplayName("should add the task")
        public void testAddTask() {
            TaskInfo taskInfo = new TaskInfo(null, "Task 1", "Description", null, HIGH, PENDING, null, null);
            Task task = new Task("1", "Task 1", "Description", null, HIGH, PENDING, null, null);
            when(taskRepository.save(taskInfo.toTask())).thenReturn(task);
            assertEquals(task, taskAggregateRoot.addTask(taskInfo.toTask()), "Expected the added task to match the original task");
            verify(taskRepository).save(taskInfo.toTask());
        }

        @Test
        @DisplayName("throws exception when task is null")
        void throwsExceptionWhenTaskIsNull() {
            assertThrows(MissingTaskException.class, () -> taskAggregateRoot.addTask(null), "Expected MissingTaskException to be thrown when adding a null task");
        }
    }

    @Nested
    @DisplayName("when updating a task")
    class UpdateTask {
        Task task;

        Task updatedTask;
        @BeforeEach
        void setUp() {
            task = new Task("1", "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), emptyList());
            updatedTask = new Task("1", "Task 1", "Description - changed", Instant.now(), HIGH, PENDING, Set.of("label1"), emptyList());
        }

        @Test
        @DisplayName("should throw an MissingTaskException when the task is null")
        public void testUpdateTask_nullTask() {
            assertThrows(MissingTaskException.class, () -> taskAggregateRoot.updateTask(null), "Task must be present when updating it");
        }

        @Test
        @DisplayName("throws exception when task not found")
        void throwsExceptionWhenTaskNotFound() {
            when(taskRepository.findById(task.id())).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.updateTask(task), format("Task with id '%s' not found", task.id()));
            verify(taskRepository).findById(task.id());
        }

        @Test
        @DisplayName("should update the task in the repository")
        public void testUpdateTask() {
            when(taskRepository.findById("1")).thenReturn(Optional.of(task));
            when(taskRepository.save(updatedTask)).thenReturn(updatedTask);
            assertEquals(updatedTask, taskAggregateRoot.updateTask(updatedTask));
            verify(taskRepository).save(updatedTask);
        }
    }

    @Nested
    @DisplayName("when marking in progress")
    class MarkInProgress {
        Task task;

        @BeforeEach
        void setUp() {
            task = new Task("1", "Task 1", "Description",null, HIGH, PENDING, Set.of("label1"), emptyList());
        }

        @Test
        @DisplayName("throws exception when task not found")
        void throwsExceptionWhenTaskNotFound() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markInProgress("2"), format("Task with id %s not found", "2"));
        }


        @Test
        @DisplayName("does not update status of different task")
        void doesNotUpdateStatusOfDifferentTask() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markInProgress("2"), format("Task with id %s not found", "2"));
        }

        @Test
        @DisplayName("updates status to IN_PROGRESS")
        void updatesStatusToInProgress() {
            when(taskRepository.findById(task.id())).thenReturn(Optional.of(task));
            Task updated = new Task(task.id(), "Task 1", "Description", null, HIGH, IN_PROGRESS, Set.of("label1"), emptyList());
            when(taskRepository.save(updated)).thenReturn(updated);
            taskAggregateRoot.markInProgress(task.id());
            verify(taskRepository).save(updated);
        }
    }

    @Nested
    @DisplayName("when marking complete")
    class MarkComplete {
        Task task;

        @BeforeEach
        void setUp() {
            task = new Task("1", "Task 1", "Description", null, HIGH, IN_PROGRESS, Set.of("label1"), emptyList());
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
            when(taskRepository.findById(task.id())).thenReturn(Optional.of(task));
            Task updated = new Task(task.id(), "Task 1", "Description", null, HIGH, COMPLETED, Set.of("label1"), emptyList());
            when(taskRepository.save(updated)).thenReturn(updated);
            taskAggregateRoot.markComplete(task.id());
            verify(taskRepository).save(updated);
        }

        @Test
        @DisplayName("does not update status of different task")
        void doesNotUpdateStatusOfDifferentTask() {
            when(taskRepository.findById("2")).thenReturn(empty());
            assertThrows(TaskNotFoundException.class, () -> taskAggregateRoot.markComplete("2"), "Expected TaskNotFoundException to be thrown when marking a task as complete but task was not found");
        }
    }

}