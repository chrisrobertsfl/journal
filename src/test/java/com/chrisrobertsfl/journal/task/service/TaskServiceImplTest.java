package com.chrisrobertsfl.journal.task.service;

import com.chrisrobertsfl.journal.task.model.*;
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
import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Given Task Service Implementation")
public class TaskServiceImplTest {
    @InjectMocks
    TaskServiceImpl taskService;

    @Mock
    TaskAggregateRoot taskAggregateRoot;

    @Nested
    @DisplayName("when finding all tasks")
    class FindAll {

        List<Task> tasks = List.of(
                new Task("1", "Task 1", "Description 1", Instant.now(), HIGH, PENDING, null, null),
                new Task("2", "Task 2", "Description 2", Instant.now(), LOW, IN_PROGRESS, null, null)
        );

        @Test
        @DisplayName("should return an empty list when no tasks are found")
        public void testFindAll_empty() {
            when(taskAggregateRoot.findAll()).thenReturn(emptyList());
            assertEquals(emptyList(), taskService.findAll(), "Incorrect tasks");
            verify(taskAggregateRoot).findAll();
        }

        @Test
        @DisplayName("should return all tasks")
        public void testFindAll() {
            when(taskAggregateRoot.findAll()).thenReturn(tasks);
            List<TaskInfo> foundTasks = taskService.findAll();

            assertAll(
                    () -> assertEquals(2, foundTasks.size(), "Incorrect number of tasks"),
                    () -> assertEquals(TaskInfo.fromTask(tasks.get(0)), foundTasks.get(0), "Incorrect task at index 0"),
                    () -> assertEquals(TaskInfo.fromTask(tasks.get(1)), foundTasks.get(1), "Incorrect task at index 1")
            );
            verify(taskAggregateRoot).findAll();
        }
    }

    @Nested
    @DisplayName("when adding a task")
    class AddTask {
        final String taskId = "1";
        TaskInfo taskInfo;

        @BeforeEach
        void setUp() {
            taskInfo = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
        }

        @Test
        @DisplayName("saves the task")
        void savesTask() {
            when(taskAggregateRoot.addTask(taskInfo.toTask())).thenReturn(taskInfo.toTask());
            assertEquals(taskInfo, taskService.addTask(taskInfo), "Task was not saved correctly");
        }

        @Test
        @DisplayName("throws exception when task is missing")
        void throwsExceptionWhenTaskIsMissing() {
            assertThatExceptionOfType(MissingTaskException.class)
                    .isThrownBy(() -> taskService.addTask(null))
                    .withMessage("Task ID cannot be null");
        }
    }

    @Nested
    @DisplayName("when updating a task")
    class UpdateTask {
        TaskInfo taskInfo;
        TaskInfo updatedTaskInfo;
        Task updatedTask;

        @BeforeEach
        void setUp() {
            taskInfo = new TaskInfo("1", "Task 1", "New Description", Instant.now(), LOW, IN_PROGRESS, Set.of("label1", "label2"), List.of());
            updatedTaskInfo = new TaskInfo("1", "Task 1", "Updated Description", Instant.now(), LOW, IN_PROGRESS, Set.of("label1", "label2"), List.of());
        }

        @Test
        @DisplayName("updates the task")
        void updatesTask() {
            when(taskAggregateRoot.updateTask(taskInfo.toTask())).thenReturn(updatedTaskInfo.toTask());
            assertEquals(updatedTaskInfo, taskService.updateTask(taskInfo), "Task was not updated correctly");
        }

        @Test
        @DisplayName("throws exception when task is missing")
        void throwsExceptionWhenTaskIsMissing() {
            when(taskAggregateRoot.updateTask(any(Task.class))).thenThrow(new TaskNotFoundException(anyString()));
            assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(updatedTaskInfo), "Expected TaskNotFoundException to be thrown when updating a task with a non-existent id");
        }
    }

    @Nested
    @DisplayName("when deleting a task")
    class DeleteTask {
        final TaskInfo taskInfo = new TaskInfo("1", "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());

        @Test
        @DisplayName("deletes a task")
        void deleteTask() {
            when(taskAggregateRoot.deleteTask(taskInfo.id())).thenReturn(taskInfo.toTask());
            TaskInfo deleted = taskService.deleteTask(taskInfo.id());
            assertAll(
                    () -> assertDoesNotThrow(() -> deleted, "Expected no exception to be thrown when deleting a task with a valid id"),
                    () -> assertEquals(taskInfo, deleted, "Expected the deleted task to match the original task"));

        }

        @Test
        @DisplayName("throws exception when id is null")
        void throwsExceptionWhenTaskIsMissing() {
            assertThatExceptionOfType(MissingTaskException.class)
                    .isThrownBy(() -> taskService.deleteTask(null))
                    .withMessage("Task ID cannot be null");
        }

        @Test
        @DisplayName("throws exception when task is not found")
        void throwsExceptionWhenTaskIsNotFound() {
            when(taskAggregateRoot.deleteTask("invalid-id")).thenThrow(new TaskNotFoundException(any()));
            assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask("invalid-id"), "Expected TaskNotFoundException to be thrown when deleting a task with a non-existent id");
        }
    }

    @Nested
    @DisplayName("when finding a task by id")
    class FindById {
        TaskInfo taskInfo;

        @BeforeEach
        void setUp() {
            taskInfo = new TaskInfo("1", "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
            taskService = new TaskServiceImpl(taskAggregateRoot);
        }

        @Test
        @DisplayName("should return the task")
        void returnsTask() {
            when(taskAggregateRoot.findById(taskInfo.id())).thenReturn(Optional.of(taskInfo.toTask()));
            Optional<TaskInfo> result = taskService.findById(taskInfo.id());
            assertAll(
                    () -> assertTrue(result.isPresent(), "Task was not found"),
                    () -> assertEquals(taskInfo, result.get(), "Incorrect task")
            );
        }

        @Test
        @DisplayName("should return empty optional when task is not found")
        void returnsEmptyOptionalWhenTaskIsNotFound() {
            when(taskAggregateRoot.findById("invalid-id")).thenReturn(Optional.empty());
            assertFalse(taskService.findById("invalid-id").isPresent(), "Task was found but should not have been");
        }
    }

    @Nested
    @DisplayName("when finding a task by label")
    class FindByLabel {
        TaskInfo taskInfo;

        @BeforeEach
        void setUp() {
            taskInfo = new TaskInfo("1", "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
        }

        @Test
        @DisplayName("should return the task")
        void returnsTask() {
            when(taskAggregateRoot.findByLabel(Set.of("label1"))).thenReturn(List.of(taskInfo.toTask()));
            List<TaskInfo> result = taskService.findByLabel(Set.of("label1"));
            assertAll(
                    () -> assertEquals(1, result.size(), "Incorrect number of tasks"),
                    () -> assertEquals(taskInfo, result.get(0), "Incorrect task")
            );
        }

        @Test
        @DisplayName("should return empty list when task is not found")
        void returnsEmptyListWhenTaskIsNotFound() {
            when(taskAggregateRoot.findByLabel(Set.of("invalid-label"))).thenReturn(List.of());
            assertTrue(taskService.findByLabel(Set.of("invalid-label")).isEmpty(), "Task was found but should not have been");
        }
    }

    @Nested
    @DisplayName("when finding tasks by status")
    class FindByStatus {
        final String taskId = "1";
        final String status = "PENDING";
        TaskInfo task;

        @BeforeEach
        void setUp() {
            task = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
            taskService = new TaskServiceImpl(taskAggregateRoot);
        }

        @Test
        @DisplayName("should return tasks with the given status")
        void returnsTasksWithGivenStatus() {
            when(taskAggregateRoot.findByStatus(PENDING)).thenReturn(List.of(task.toTask()));
            List<TaskInfo> result = taskService.findByStatus(status);
            assertAll(
                    () -> assertEquals(1, result.size(), "Incorrect number of tasks returned"),
                    () -> assertEquals(task, result.get(0), "Incorrect task returned")
            );
        }

        @Test
        @DisplayName("should return empty list when no tasks have the given status")
        void returnsEmptyListWhenNoTasksHaveGivenStatus() {
            when(taskAggregateRoot.findByStatus(PENDING)).thenReturn(List.of());
            assertTrue(taskService.findByStatus(status).isEmpty(), "Tasks were returned but none should have been");
        }
    }

    @Nested
    @DisplayName("when marking a task in progress")
    class MarkInProgress {
        final String taskId = "1";
        TaskInfo taskInfo;

        @BeforeEach
        void setUp() {
            taskInfo = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
        }

        @Test
        @DisplayName("should update the status to IN_PROGRESS")
        void updatesStatusToInProgress() {
            Task marked = new Task(taskInfo.id(), taskInfo.name(), taskInfo.description(), taskInfo.createdAt(), taskInfo.priority(), IN_PROGRESS, taskInfo.labels(), List.of());
            when(taskAggregateRoot.markInProgress(taskInfo.id())).thenReturn(marked);
            assertEquals(IN_PROGRESS, taskService.markInProgress(taskId).status(), "Expected task status to be IN_PROGRESS");
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task is not found")
        void throwsExceptionWhenTaskIsNotFound() {
            when(taskAggregateRoot.markInProgress("invalid-id")).thenThrow(new TaskNotFoundException("Task with ID '%s' not found"));
            taskService = new TaskServiceImpl(taskAggregateRoot);
            assertThrows(TaskNotFoundException.class, () -> taskService.markInProgress("invalid-id"), "Expected TaskNotFoundException but no exception was thrown");
        }

        @Nested
        @DisplayName("when marking a task as complete")
        class MarkComplete {
            TaskInfo taskInfo;

            @BeforeEach
            void setUp() {
                taskInfo = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
                taskService = new TaskServiceImpl(taskAggregateRoot);
            }

            @Test
            @DisplayName("should mark the task as complete")
            void marksTaskAsComplete() {
                Task marked = new Task(taskInfo.id(), taskInfo.name(), taskInfo.description(), taskInfo.createdAt(), taskInfo.priority(), COMPLETED, taskInfo.labels(), List.of());
                when(taskAggregateRoot.markComplete(taskInfo.id())).thenReturn(marked);
                assertEquals(COMPLETED, taskService.markComplete(taskInfo.id()).status(), "Task status should have been COMPLETED");
            }

            @Test
            @DisplayName("should throw an exception when the task is not found")
            void throwsExceptionWhenTaskNotFound() {
                when(taskAggregateRoot.markComplete("invalid-id")).thenThrow(new TaskNotFoundException("Task with ID 'invalid-id' not found"));
                assertThrows(TaskNotFoundException.class, () -> taskService.markComplete("invalid-id"), "Task was found but should not have been");
            }
        }
    }

}
