package com.chrisrobertsfl.journal;

import com.chrisrobertsfl.journal.task.model.TaskAggregateRoot;
import com.chrisrobertsfl.journal.task.repository.TaskRepository;
import com.chrisrobertsfl.journal.task.service.TaskService;
import com.chrisrobertsfl.journal.task.service.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
public class TaskConfiguration {

    @Autowired
    TaskRepository taskRepository;


    @Bean(name = "taskService")
    public TaskService taskService(TaskAggregateRoot taskAggregateRoot) {
        return new TaskServiceImpl(taskAggregateRoot);
    }

    @Bean(name = "taskAggregator")
    public TaskAggregateRoot taskAggregateRoot(TaskRepository taskRepository) {
        return new TaskAggregateRoot(taskRepository);
    }
}
