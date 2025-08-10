package com.richard.task;

import com.richard.task.error.TaskException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/origins")
public class OriginController {

    private final OriginRepository repo;

    @Autowired
    public OriginController(OriginRepository repo){
        this.repo = repo;
    }


    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode="201", description = "Created"),
            @ApiResponse(responseCode ="209", description = "Already exists")
    })
    private ResponseEntity<Void> createOrigin(@Valid @RequestBody Origin payload) throws Exception {
        if (repo.existsByUri(payload.uri()))
            throw new TaskException("Already exists", HttpStatus.CONFLICT);
        repo.save(payload);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    private ResponseEntity<Origin> findById(@PathVariable Long id) throws TaskException {
        if (!repo.existsById(id)) throw new TaskException("Not Found", HttpStatus.NOT_FOUND);
        Optional<Origin> found = repo.findById(id);
        if (found.isPresent()) {
            Origin origin = found.get();
            return ResponseEntity.ok(origin);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok")
    })
    private ResponseEntity<List<Origin>> findAll() {
        return ResponseEntity.ok(repo.findAllOrigins());
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    private ResponseEntity<Void> updateOrigin(@PathVariable Long id, @Valid @RequestBody Origin payload) throws Exception{
        Optional<Origin> optionalOrigin = repo.findById(id);
        if (optionalOrigin.isPresent()){
            Origin originToUpdate = optionalOrigin.get();
            Origin updatedOrigin = new Origin(
                    originToUpdate.id(),
                    payload.uri()
            );
            repo.save(updatedOrigin);
            return ResponseEntity.noContent().build();
        }
        throw new TaskException("Not Found", HttpStatus.NOT_FOUND);

    }


    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    private ResponseEntity<Void> deleteById(@PathVariable Long id) throws Exception {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new TaskException("Not Found", HttpStatus.NOT_FOUND);
    }
}
