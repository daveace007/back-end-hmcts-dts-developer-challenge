package com.richard.task;


import com.richard.task.error.TaskException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {


    private final TaskRepository repository;

    @Autowired
    private TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode="201", description = "Created"),
            @ApiResponse(responseCode ="209", description = "Task with this title already exists")
    })
    private ResponseEntity<Void> createTask(@RequestBody @Valid Task newTaskRequest, UriComponentsBuilder ucb) throws Exception{
        if (repository.existsByTitle(newTaskRequest.title())){
            String message = "Task with title: %s already exists".formatted(newTaskRequest.title());
            throw new TaskException(message, HttpStatus.CONFLICT);
        }
        Task task = new Task(
                null,
                newTaskRequest.title(),
                newTaskRequest.description(),
                newTaskRequest.status(),
                newTaskRequest.dueDateTime()
        );
        Task savedTask = repository.save(task);
        URI taskLocation = ucb.path("api/tasks/{id}")
                .buildAndExpand(savedTask.id())
                .toUri();
        return ResponseEntity.created(taskLocation).build();
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    private ResponseEntity<Task> findById(@PathVariable Long id) {
        Optional<Task> optionalTask = repository.findById(id);
        if (optionalTask.isPresent()) {
            Task foundTask = optionalTask.get();
            return ResponseEntity.ok(foundTask);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok")
    })
    @PageableAsQueryParam
    private ResponseEntity<Page<Task>> findAll(Pageable pageDetails) {
        PageRequest request = getPageRequest(pageDetails);
        Page<Task> page = repository.findAll(request);
        return ResponseEntity.ok(page);
    }



    @GetMapping("/search-title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok")
    })
    @PageableAsQueryParam
    private ResponseEntity<Page<Task>> findByTitle(@RequestParam("title")String title, Pageable pageable){
        PageRequest pageRequest = getPageRequest(pageable);
        Page<Task> page = repository.findByTitleContainingIgnoreCase(title, pageRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search-status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok")
    })
    @PageableAsQueryParam
    private ResponseEntity<Page<Task>> findByStatus(@RequestParam("status") String status, Pageable pageDetails){
        PageRequest request = getPageRequest(pageDetails);
        Page<Task> page = repository.findByStatus(status, request);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    private ResponseEntity<Void> updateTask(@PathVariable Long id, @Valid @RequestBody Task payload) {
        if (repository.existsById(id)) {
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
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    private ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private PageRequest getPageRequest(Pageable pageDetails) {
        return PageRequest.of(
                pageDetails.getPageNumber(),
                pageDetails.getPageSize(),
                pageDetails.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
        );
    }

}
