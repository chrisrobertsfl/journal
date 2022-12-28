package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.TaskInfo;
import com.chrisrobertsfl.journal.task.model.TaskResponse;
import com.chrisrobertsfl.journal.task.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

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

        @Test
        @DisplayName("should return a task response with the added task")
        void returnsTaskResponse() {
            TaskInfo task = new TaskInfo("1", "Task 1", "Description", null, null, null, null, null);
            doReturn(task).when(taskService).addTask(any(TaskInfo.class));

            ResponseEntity<TaskResponse> response = taskController.addTask(task);

            assertAll(
                    () -> assertNotNull(response.getBody().task(), "Task response body should not be null"),
                    () -> assertEquals(task, response.getBody().task(), "Incorrect task in response body")
            );
        }
    }
}

