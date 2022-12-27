package com.chrisrobertsfl.journal.task.service;

import com.chrisrobertsfl.journal.task.model.*;
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
import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Service")
public class TaskServiceImplTest {
    TaskService taskService;
    @Mock
    TaskRepository taskRepository;

    TaskAggregateRoot taskAggregateRoot;


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
            taskService = new TaskServiceImpl(taskRepository);
            taskAggregateRoot = ((TaskServiceImpl) taskService).taskAggregateRoot;
        }


        @Test
        @DisplayName("should return an empty list when no tasks are found")
        public void testFindAll_empty() {
            when(taskRepository.findAll()).thenReturn(emptyList());
            List<TaskInfo> result = taskService.findAll();
            assertEquals(emptyList(), result, "Incorrect tasks");
            verify(taskRepository).findAll();
        }

        @Test
        @DisplayName("should return all tasks")
        public void testFindAll() {
            when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
            List<TaskInfo> tasks = taskService.findAll();

            assertAll(
                    () -> assertEquals(2, tasks.size(), "Incorrect number of tasks"),
                    () -> assertEquals(TaskInfo.fromTask(task1), tasks.get(0), "Incorrect task at index 0"),
                    () -> assertEquals(TaskInfo.fromTask(task2), tasks.get(1), "Incorrect task at index 1")
            );
            verify(taskRepository).findAll();
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
            taskService = new TaskServiceImpl(taskRepository);
        }

        @Test
        @DisplayName("saves the task")
        void savesTask() {
            when(taskRepository.save(taskInfo.toTask())).thenReturn(taskInfo.toTask());
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
        final String taskId = "1";
        TaskInfo taskInfo;
        TaskInfo updatedTaskInfo;
        Task updatedTask;

        @BeforeEach
        void setUp() {
            taskService = new TaskServiceImpl(taskRepository);
            taskInfo = new TaskInfo(taskId, "Task 1", "New Description", Instant.now(), LOW, IN_PROGRESS, Set.of("label1", "label2"), List.of());
            updatedTaskInfo = new TaskInfo(taskId, "Task 1", "Updated Description", Instant.now(), LOW, IN_PROGRESS, Set.of("label1", "label2"), List.of());
        }

        @Test
        @DisplayName("updates the task")
        void updatesTask() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskInfo.toTask()));
            when(taskRepository.save(taskInfo.toTask())).thenReturn(taskInfo.toTask());
            assertEquals(taskInfo, taskService.updateTask(taskInfo), "Task was not updated correctly");
        }

        @Test
        @DisplayName("throws exception when task is missing")
        void throwsExceptionWhenTaskIsMissing() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
            assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(updatedTaskInfo), "Expected TaskNotFoundException to be thrown when updating a task with a non-existent id");
        }
    }

    @Nested
    @DisplayName("when deleting a task")
    class DeleteTask {
        final String taskId = "1";
        final TaskInfo taskInfo = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());

        @BeforeEach
        void setUp() {
            taskService = new TaskServiceImpl(taskRepository);
        }

        @Test
        @DisplayName("deletes a task")
        void deleteTask() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskInfo.toTask()));
            TaskInfo deleted = taskService.deleteTask(taskId);
            assertAll(
                    () -> assertDoesNotThrow(() -> deleted, "Expected no exception to be thrown when deleting a task with a valid id"),
                    () -> assertEquals(taskInfo, deleted));

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
            assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask("invalid-id"), "Expected TaskNotFoundException to be thrown when deleting a task with a non-existent id");
        }
    }

    @Nested
    @DisplayName("when finding a task by id")
    class FindById {
        final String taskId = "1";
        TaskInfo task;

        @BeforeEach
        void setUp() {
            task = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
            taskService = new TaskServiceImpl(taskRepository);
        }

        @Test
        @DisplayName("should return the task")
        void returnsTask() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task.toTask()));
            Optional<TaskInfo> result = taskService.findById(taskId);
            assertAll(
                    () -> assertTrue(result.isPresent(), "Task was not found"),
                    () -> assertEquals(task, result.get(), "Incorrect task")
            );
        }

        @Test
        @DisplayName("should return empty optional when task is not found")
        void returnsEmptyOptionalWhenTaskIsNotFound() {
            when(taskRepository.findById("invalid-id")).thenReturn(Optional.empty());
            assertFalse(taskService.findById("invalid-id").isPresent(), "Task was found but should not have been");
        }
    }

    @Nested
    @DisplayName("when finding a task by label")
    class FindByLabel {
        final String taskId = "1";
        TaskInfo task;

        @BeforeEach
        void setUp() {
            task = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
            taskService = new TaskServiceImpl(taskRepository);
        }

        @Test
        @DisplayName("should return the task")
        void returnsTask() {
            when(taskRepository.findByLabelsIn(Set.of("label1"))).thenReturn(List.of(task.toTask()));
            List<TaskInfo> result = taskService.findByLabel(Set.of("label1"));
            assertAll(
                    () -> assertEquals(1, result.size(), "Incorrect number of tasks"),
                    () -> assertEquals(task, result.get(0), "Incorrect task")
            );
        }

        @Test
        @DisplayName("should return empty list when task is not found")
        void returnsEmptyListWhenTaskIsNotFound() {
            when(taskRepository.findByLabelsIn(Set.of("invalid-label"))).thenReturn(List.of());
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
            taskService = new TaskServiceImpl(taskRepository);
        }

        @Test
        @DisplayName("should return tasks with the given status")
        void returnsTasksWithGivenStatus() {
            when(taskRepository.findByStatus(PENDING)).thenReturn(List.of(task.toTask()));
            List<TaskInfo> result = taskService.findByStatus(status);
            assertAll(
                    () -> assertEquals(1, result.size(), "Incorrect number of tasks returned"),
                    () -> assertEquals(task, result.get(0), "Incorrect task returned")
            );
        }

        @Test
        @DisplayName("should return empty list when no tasks have the given status")
        void returnsEmptyListWhenNoTasksHaveGivenStatus() {
            when(taskRepository.findByStatus(PENDING)).thenReturn(List.of());
            assertTrue(taskService.findByStatus(status).isEmpty(), "Tasks were returned but none should have been");
        }
    }

    @Nested
    @DisplayName("when marking a task in progress")
    class MarkInProgress {
        final String taskId = "1";
        TaskInfo task;

        @BeforeEach
        void setUp() {
            task = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
            taskService = new TaskServiceImpl(taskRepository);
        }

        @Test
        @DisplayName("should update the status to IN_PROGRESS")
        void updatesStatusToInProgress() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task.toTask()));
            Task marked = new Task(task.id(), task.name(), task.description(), task.createdAt(), task.priority(), IN_PROGRESS, task.labels(), List.of());
            when(taskRepository.save(marked)).thenReturn(marked);
            assertEquals(IN_PROGRESS, taskService.markInProgress(taskId).status(), "Expected task status to be IN_PROGRESS");
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task is not found")
        void throwsExceptionWhenTaskIsNotFound() {
            when(taskRepository.findById("invalid-id")).thenReturn(Optional.empty());
            assertThrows(TaskNotFoundException.class, () -> taskService.markInProgress("invalid-id"), "Expected TaskNotFoundException but no exception was thrown");
        }

        @Nested
        @DisplayName("when marking a task as complete")
        class MarkComplete {
            final String taskId = "1";
            TaskInfo task;

            @BeforeEach
            void setUp() {
                task = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
                taskService = new TaskServiceImpl(taskRepository);
            }

            @Test
            @DisplayName("should mark the task as complete")
            void marksTaskAsComplete() {
                when(taskRepository.findById(taskId)).thenReturn(Optional.of(task.toTask()));
                Task marked = new Task(task.id(), task.name(), task.description(), task.createdAt(), task.priority(), COMPLETED, task.labels(), List.of());
                when(taskRepository.save(marked)).thenReturn(marked);
                assertEquals(COMPLETED, taskService.markComplete(taskId).status(), "Task status should have been COMPLETED");
            }

            @Test
            @DisplayName("should throw an exception when the task is not found")
            void throwsExceptionWhenTaskNotFound() {
                when(taskRepository.findById("invalid-id")).thenReturn(Optional.empty());
                assertThrows(TaskNotFoundException.class, () -> taskService.markComplete("invalid-id"), "Task was found but should not have been");
            }
        }
    }
}
