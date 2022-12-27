package com.chrisrobertsfl.journal;

import com.chrisrobertsfl.coreengine.CoreEngine;
import com.chrisrobertsfl.coreengine.Drools8Engine;
import com.chrisrobertsfl.journal.repository.ItemRepository;
import com.chrisrobertsfl.journal.service.JournalEngine;
import com.chrisrobertsfl.journal.service.TaskService;
import com.chrisrobertsfl.journal.service.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
public class TaskConfiguration {

    @Autowired
    ItemRepository itemRepository;


    @Bean(name = "taskService")
    public TaskService taskService(ItemRepository itemRepository, @Qualifier("journalEngine") CoreEngine engine) {
        return new TaskServiceImpl(itemRepository, engine);
    }

    @Bean(name = "journalEngine")
    CoreEngine coreEngine() {
        return new JournalEngine(Drools8Engine.builder()
                .consumer(TaskServiceImpl.logger::info)
                .ruleFiles("src/main/resources/drl/task.drl")
                .build()
                .init());
    }
}
