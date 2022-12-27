package com.chrisrobertsfl.journal.task.repository;

import com.chrisrobertsfl.journal.task.model.Status;
import com.chrisrobertsfl.journal.task.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByLabelsIn(Set<String> labels);

    List<Task> findByStatus(Status status);
}
