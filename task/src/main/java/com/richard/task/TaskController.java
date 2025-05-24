package com.richard.task;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {


    private final TaskRepository repository;

    @Autowired
    private TaskController(TaskRepository repository){
        this.repository = repository;
    }

    @PostMapping
    private ResponseEntity<Void> createTask(@RequestBody @Valid Task newTaskRequest, UriComponentsBuilder ucb){
        Task task = new Task(
                null,
                newTaskRequest.title(),
                newTaskRequest.description(),
                newTaskRequest.status(),
                newTaskRequest.dueDateTime()
        );
        Task savedTask = repository.save(task);
        URI taskLocation = ucb.path("tasks/{id}")
                .buildAndExpand(savedTask.id())
                .toUri();
        return ResponseEntity.created(taskLocation).build();
    }

    @GetMapping("/{id}")
    private ResponseEntity<Task> findById(@PathVariable Long id){
        Optional<Task> optionalTask = repository.findById(id);
        if (optionalTask.isPresent()){
            Task foundTask = optionalTask.get();
            return ResponseEntity.ok(foundTask);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    private ResponseEntity<List<Task>> findAll(Pageable pageDetails){
        PageRequest request = PageRequest.of(
                pageDetails.getPageNumber(),
                pageDetails.getPageSize(),
                pageDetails.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
        );
        Page<Task> page = repository.findAll(request);
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> updateTask(@PathVariable Long id, @Valid @RequestBody Task payload){
        if (repository.existsById(id)){
            Task updatedTask = new Task(
                    id,
                    payload.title(),
                    payload.description(),
                    payload.status(),
                    payload.dueDateTime()
            );
            repository.save(updatedTask);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteByTask(@PathVariable Long id){
        if (repository.existsById(id)){
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}
