package com.chrisrobertsfl.journal.service;

import com.chrisrobertsfl.coreengine.CoreEngine;
import com.chrisrobertsfl.journal.model.State;
import com.chrisrobertsfl.journal.model.TaskInfo;
import com.chrisrobertsfl.journal.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    public static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    CoreEngine engine;
    ItemRepository itemRepository;

    public TaskServiceImpl(ItemRepository itemRepository, CoreEngine engine) {
        this.itemRepository = itemRepository;
        this.engine = engine;
    }

    @Override
    public TaskInfo create(TaskInfo taskInfo) {
        TaskInfo saved = engine
                .insert(taskInfo)
                .insert(itemRepository)
                .run()
                .dumpRules()
                .findAll(TaskInfo.class)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find task"));
        return saved;
    }
}
