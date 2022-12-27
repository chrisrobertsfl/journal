package com.chrisrobertsfl.journal.service;

import com.chrisrobertsfl.coreengine.Drools8Engine;
import com.chrisrobertsfl.journal.Tests;
import com.chrisrobertsfl.journal.model.Task;
import com.chrisrobertsfl.journal.model.TaskInfo;
import com.chrisrobertsfl.journal.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.chrisrobertsfl.journal.model.State.NOT_STARTED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TaskServiceImplTest {
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImplTest.class);

    ItemRepository itemRepository;

    TaskServiceImpl service;

    @BeforeEach
    void beforeEachTest() {
        itemRepository = mock(ItemRepository.class);
        service = taskService();
    }

    @Test
    void createNewTasks() {
        log.info("Hello world");
        when(itemRepository.save(Mockito.any(Task.class))).thenReturn(new Task(
                UUID.randomUUID().toString(),
                "Get some food",
                "Food is from publix",
                NOT_STARTED,
                Set.of("label 1", "label 2")));
        TaskInfo saved = service.create(new TaskInfo(null, "Get some food", "Food is from publix", null, Set.of("label 1", "label 2")));
        System.out.println("saved = " + saved);
        assertAll(
                () -> assertEquals(NOT_STARTED, saved.state(), "state should be not started"),
                () -> assertEquals("Get some food", saved.name(), "name is different"),
                () -> assertEquals("Food is from publix", saved.description(), "description is different"),
                () -> assertEquals(Set.of("label 1", "label 2"), saved.labels(), "labels are different"),
                () -> assertFalse(Objects.isNull(saved.id()), "no id")
        );
    }

    public TaskServiceImpl taskService() {
        String ruleFile = Tests.under(this).file("task-item.drl");
        Drools8Engine engine = Drools8Engine.builder()
                .consumer(log::info)
                .ruleFiles(ruleFile)
                .build()
                .init();
        return new TaskServiceImpl(itemRepository, engine);
    }

}

