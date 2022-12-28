package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.*;
import com.chrisrobertsfl.journal.task.service.TaskService;
import com.chrisrobertsfl.journal.task.service.TaskServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.chrisrobertsfl.journal.task.model.Priority.HIGH;
import static com.chrisrobertsfl.journal.task.model.Status.PENDING;
import static com.chrisrobertsfl.journal.task.model.TaskInfo.nullTaskInfo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Given Task Controller")
class TaskControllerTest {

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskController taskController;

    @Nested
    @DisplayName("when adding a task")
    class AddTask {
        static Stream<Arguments> provideExceptionsAndStatusCodes() {
            return Stream.of(
                    Arguments.of(new MissingTaskException("Task ID cannot be null"), HttpStatusCode.valueOf(400), "Task ID cannot be null"),
                    Arguments.of(new IllegalArgumentException("Something bad happened"), HttpStatusCode.valueOf(500), "Something bad happened")
            );
        }

        @Test
        @DisplayName("should return a task response with the added task")
        void returnsTaskResponse() {
            TaskInfo task = new TaskInfo("1", "Task 1", "Description", null, null, null, null, null);
            when(taskService.addTask(task)).thenReturn(task);

            ResponseEntity<TaskResponse> response = taskController.addTask(task);

            assertAll(
                    () -> assertNotNull(response.getBody().task(), "Task response body should not be null"),
                    () -> assertEquals(task, response.getBody().task(), "Incorrect task in response body")
            );
        }

        @ParameterizedTest(name = "Status of {1} should be returned with message \"{2}\"")
        @MethodSource("provideExceptionsAndStatusCodes")
        @DisplayName("When adding an erroneous task")
        void returnsCorrectResponseWhenExceptionIsThrown(Exception exception, HttpStatusCode code, String message) {
            when(taskService.addTask(null)).thenThrow(exception);
            ResponseEntity<TaskResponse> response = taskController.addTask(null);
            assertAll(
                    () -> assertEquals(code, response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals(message, response.getBody().error(), "Incorrect error message")
            );
        }
    }

    @Nested
    @DisplayName("when updating a task")
    class UpdateTask {

        @Test
        @DisplayName("When updating task that whose id is null")
        void returnsMissingTaskExceptionWhenIdIsNull() {
            when(taskService.updateTask(nullTaskInfo())).thenThrow(new MissingTaskException("Task ID cannot be null"));
            ResponseEntity<TaskResponse> response = taskController.updateTask(nullTaskInfo());
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode()),
                    () -> assertEquals("Task ID cannot be null", response.getBody().error(), "Incorrect error message")
            );
        }

        @Test
        @DisplayName("When updating task that is not found")
        void returnsTaskNotFoundExceptionWhenCannotFindTask() {
            TaskInfo task = new TaskInfo("1", null, null, null, null, null, null, null);
            when(taskService.updateTask(task)).thenThrow(new TaskNotFoundException("Task with ID '1' not found"));
            ResponseEntity<TaskResponse> response = taskController.updateTask(task);
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals("Task with ID '1' not found", response.getBody().error(), "Incorrect error message")
            );
        }

    }

    @Nested
    @DisplayName("when deleting a task")
    class DeleteTask {

        @Test
        @DisplayName("task is successfully deleted")
        void deleteTask() {
            TaskInfo task = new TaskInfo("1", null, null, null, null, null, null, null);
            ResponseEntity<TaskResponse> response = taskController.deleteTask(task.id());
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode(), "Incorrect status code")
            );
        }

        @Test
        @DisplayName("id is null")
        void deleteTaskWithIdNull() {
            when(taskService.deleteTask(null)).thenThrow(new MissingTaskException("Task ID cannot be null"));
            ResponseEntity<TaskResponse> response = taskController.deleteTask(null);
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals("Task ID cannot be null", response.getBody().error(), "Incorrect error message")
            );
        }

        @Test
        @DisplayName("task is not found")
        void deleteTaskWithTaskNotFound() {
            when(taskService.deleteTask("1")).thenThrow(new TaskNotFoundException("Task with ID '1' not found"));
            ResponseEntity<TaskResponse> response = taskController.deleteTask("1");
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals("Task with ID '1' not found", response.getBody().error(), "Incorrect error message")
            );
        }
    }

    @Nested
    @DisplayName("when finding a task by id")
    class FindById {

        @Test
        @DisplayName("task is found")
        void findById() {
            TaskInfo task = new TaskInfo("1", null, null, null, null, null, null, null);
            when(taskService.findById("1")).thenReturn(Optional.of(task));
            ResponseEntity<TaskResponse> response = taskController.findById("1");
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode(), "Incorrect status code")
            );
        }

        @Test
        @DisplayName("task id is null")
        void findTaskWithIdNull() {
            when(taskService.findById(null)).thenThrow(new MissingTaskException("Task ID cannot be null"));
            ResponseEntity<TaskResponse> response = taskController.findById(null);
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals("Task ID cannot be null", response.getBody().error(), "Incorrect error message")
            );
        }

        @Test
        @DisplayName("task is not found")
        void findTaskWithTaskNotFound() {
            when(taskService.findById("1")).thenThrow(new TaskNotFoundException("Task with ID '1' not found"));
            ResponseEntity<TaskResponse> response = taskController.findById("1");
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals("Task with ID '1' not found", response.getBody().error(), "Incorrect error message")
            );
        }
    }

    @Nested
    @DisplayName("when finding tasks by labels")
            //            task = new TaskInfo(taskId, "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label1"), List.of());
    class FindByLabel {

        @Test
        @DisplayName("tasks are found by labels")
        void findByLabel() {
            List<TaskInfo> taskList = List.of(
                    new TaskInfo("1", "Task 1", "Description", Instant.now(), HIGH, PENDING, Set.of("label 1"), List.of()),
                    new TaskInfo("2", "Task 2", "Description", Instant.now(), HIGH, PENDING, Set.of("label 2"), List.of())
            );
            Set<String> labels = Set.of("label 1", "label 2");
            when(taskService.findByLabel(labels)).thenReturn(taskList);
            TaskController taskController = new TaskController(taskService);
            ResponseEntity<TaskListResponse> response = taskController.findByLabel(labels);
            assertAll(
                    () -> assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode(), "Incorrect status code"),
                    () -> assertEquals(taskList, response.getBody().tasks(), "Incorrect list of tasks")
            );
        }
    }

}