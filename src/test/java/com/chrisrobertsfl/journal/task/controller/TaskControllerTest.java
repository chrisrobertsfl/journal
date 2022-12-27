package com.chrisrobertsfl.journal.task.controller;

import com.chrisrobertsfl.journal.task.model.TaskInfo;
import com.chrisrobertsfl.journal.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.chrisrobertsfl.journal.task.model.Priority.HIGH;
import static com.chrisrobertsfl.journal.task.model.Priority.LOW;
import static com.chrisrobertsfl.journal.task.model.Status.COMPLETED;
import static com.chrisrobertsfl.journal.task.model.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Controller")
public class TaskControllerTest {

    @Nested
    @DisplayName("when finding all tasks")
    public class FindAll {

        @Mock
        private TaskService taskService;

        @InjectMocks
        private TaskController taskController;

        private List<TaskInfo> tasks;

        @BeforeEach
        @DisplayName("Setup tasks")
        void setup() {
            tasks = List.of(
                    new TaskInfo("1", "name", "description", Instant.now(), LOW, IN_PROGRESS, Set.of("label1"), null),
                    new TaskInfo("2", "name2", "description2", Instant.now(), HIGH, COMPLETED, Set.of("label2"), null));
        }

        @Test
        @DisplayName("Verify task list is returned")
        void whenFindAll_thenReturnTaskList() {
            when(taskService.findAll()).thenReturn(tasks);

            ResponseEntity<List<TaskInfo>> response = taskController.findAll();
            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK."),
                    () -> assertEquals(tasks, response.getBody(), "Returned task list should match expected tasks."));
            verify(taskService).findAll();

        }
    }
}
