package com.richard.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskRepository extends CrudRepository<Task, Long>,PagingAndSortingRepository<Task, Long> {

    boolean existsByTitle(String title);

    Page<Task> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Task> findByStatus(String status, Pageable pageable);
}
