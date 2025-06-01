package com.richard.task;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskRepository extends CrudRepository<Task, Long>,PagingAndSortingRepository<Task, Long> {
    boolean existsByTitle(String title);
}
