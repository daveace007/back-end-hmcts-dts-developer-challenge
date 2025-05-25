package com.richard.task;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import static com.richard.task.Status.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskApplicationTests {

    @Autowired
    TestRestTemplate template;

    @Autowired
    TaskApplication application;

    @Autowired
    TaskController controller;

    @Autowired
    TaskRepository repository;

    @Test
    void contextLoads() {
        assertThat(application).isNotNull();
        assertThat(controller).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Test
    void shouldCreateANewTask() {
        Task task = new Task(
                null,
                "Back-end Task",
                "Develop and test back-end task",
                IN_PROGRESS.label(),
                LocalDateTime.of(3000, 5, 23, 13, 48, 30)
        );

        ResponseEntity<Void> createResponse = template.postForEntity("/tasks", task, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);

        URI locationOfNewTask = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = template.getForEntity(locationOfNewTask, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        String title = context.read("$.title");
        String description = context.read("$.description");
        String status = context.read("$.status");
        String dueDateTime = context.read("$.dueDateTime");

        assertThat(id).isNotNull();
        assertThat(title).isEqualTo("Back-end Task");
        assertThat(description).isEqualTo("Develop and test back-end task");
        assertThat(status).isEqualTo("In Progress");
        assertThat(dueDateTime).isEqualTo("3000-05-23T13:48:30");


    }

    @Test
    void shouldNotCreateANewTaskWithInvalidTitle() {
        //define a task with an invalid title
        Task task = new Task(
                null,
                "<script>console.log('Back-end Task');</script>",
                "Develop and test back-end task",
                IN_PROGRESS.label(),
                LocalDateTime.of(3000, 5, 23, 13, 48, 30)
        );
        //make a request to create the task
        HttpEntity<Task> createRequest = new HttpEntity<>(task);
        ResponseEntity<Void> createResponse = template.exchange("/tasks", POST, createRequest, Void.class);
        //confirm bad request (400) status code
        assertThat(createResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewTaskWithInvalidDescription() {
        //define a task with an invalid description
        Task task = new Task(
                null,
                "Back-end Task",
                "Develop and test back-end task%",
                IN_PROGRESS.label(),
                LocalDateTime.of(3000, 5, 23, 13, 48, 30)
        );
        //make a request to create the task
        HttpEntity<Task> createRequest = new HttpEntity<>(task);
        ResponseEntity<Void> createResponse = template.exchange("/tasks", POST, createRequest, Void.class);
        //confirm bad request (400) status code
        assertThat(createResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewTaskWithInvalidStatus() {
        //define a task with an invalid status
        Task task = new Task(
                null,
                "Back-end Task",
                "Develop and test back-end task",
                "N/A",
                LocalDateTime.of(3000, 5, 23, 13, 48, 30)
        );
        //make a request to create the task
        HttpEntity<Task> createRequest = new HttpEntity<>(task);
        ResponseEntity<Void> createResponse = template.exchange("/tasks", POST, createRequest, Void.class);
        //confirm bad request (400) status code
        assertThat(createResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewTaskWithPastDueDateTime() {
        //define a task with an invalid due date/time
        Task task = new Task(
                null,
                "Back-end Task",
                "Develop and test back-end task",
                IN_PROGRESS.label(),
                LocalDateTime.of(2024, 5, 23, 13, 48, 30)
        );
        //make a request to create the task
        HttpEntity<Task> createRequest = new HttpEntity<>(task);
        ResponseEntity<Void> createResponse = template.exchange("/tasks", POST, createRequest, Void.class);
        //confirm bad request (400) status code
        assertThat(createResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }


    @Test
    void shouldReturnATask() {
        ResponseEntity<String> response = template.getForEntity("/tasks/2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        String title = context.read("$.title");
        String description = context.read("$.description");
        String status = context.read("$.status");
        String dueDateTime = context.read("$.dueDateTime");

        assertThat(id).isEqualTo(2);
        assertThat(title).isEqualTo("Front-end Task");
        assertThat(description).isEqualTo("Develop front-end task");
        assertThat(status).isEqualTo("To do");
        assertThat(dueDateTime).isEqualTo("2025-05-23T13:50:30");
    }

    @Test
    void shouldNotReturnATaskWithAnUnknownId() {
        ResponseEntity<String> response = template.getForEntity("/task/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnAllTasks() {
        ResponseEntity<String> response = template.getForEntity("/tasks", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        int tasksCount = context.read("$.length()");
        assertThat(tasksCount).isEqualTo(2);
        JSONArray ids = context.read("$..id");
        JSONArray titles = context.read("$..title");
        JSONArray descriptions = context.read("$..description");
        JSONArray statuses = context.read("$..status");
        JSONArray dueDateTimes = context.read("$..dueDateTime");

        assertThat(ids).containsExactlyInAnyOrder(2, 1);
        assertThat(titles).contains("Front-end Task", "Back-end Task");
        assertThat(descriptions).contains("Develop and test back-end task", "Develop front-end task");
        assertThat(statuses).contains("To do", "In Progress");
        assertThat(dueDateTimes).contains("2025-05-23T13:48:30", "2025-05-23T13:50:30");
    }

    @Test
    void shouldReturnAPageOfTasks() {
        ResponseEntity<String> response = template
                .getForEntity("/tasks?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        JSONArray page = context.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfTask() {
        ResponseEntity<String> response = template
                .getForEntity("/tasks?page=0&size=1&sort=dueDateTime,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        JSONArray page = context.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        String dueDateTime = context.read("$[0].dueDateTime");
        assertThat(dueDateTime).isEqualTo("2025-05-23T13:50:30");
    }

    @Test
    void shouldReturnASortedPageOfTaskWithNoParametersButDefaultValues() {
        ResponseEntity<String> response = template
                .getForEntity("/tasks", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        JSONArray page = context.read("$[*]");
        assertThat(page.size()).isEqualTo(2);

        JSONArray ids = context.read("$..id");
        assertThat(ids).containsExactly(1, 2);
    }

    @Test
    void shouldUpdateExistingTask() {
        //create a task for update
        Task taskUpdate = new Task(null, "Front-end Task", "Develop Front-end task", Status.COMPLETED.label(), LocalDateTime.of(3000, 5, 23, 13, 30, 30));
        //request an update
        HttpEntity<Task> request = new HttpEntity<>(taskUpdate);
        //request the  updated task
        ResponseEntity<Void> updateResponse = template.exchange("/tasks/2", PUT, request, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(NO_CONTENT);

        //verify update operation
        ResponseEntity<String> getResponse = template.getForEntity("/tasks/2", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        String status = context.read("$.status");
        assertThat(id).isEqualTo(2);
        assertThat(status).isEqualTo("Completed");
    }

    @Test
    void shouldNotUpdateATaskThatDoesNotExist() {
        //create an unknown task
        Task unknownTask = new Task(null, "Deploy Application", "Deploy to AWS", Status.PENDING.label(), LocalDateTime.of(3000, 5, 23, 13, 30, 30));

        //perform an update request
        HttpEntity<Task> request = new HttpEntity<>(unknownTask);
        ResponseEntity<Void> response = template.exchange("/tasks/99999", PUT, request, Void.class);

        //confirm that task is non-existent
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldDeleteTask() {
        //create a delete request
        ResponseEntity<Void> deleteResponse = template.exchange(
                "/tasks/1", DELETE, null, Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);
        //Get the deleted task
        ResponseEntity<String> getResponse = template.getForEntity("/task/1", String.class);
        //confirm that deleted task is not found
        assertThat(getResponse.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotDeleteATaskThatDoesNotExist() {
        // create a delete request
        ResponseEntity<Void> deleteResponse = template.exchange(
                "/tasks/999999", DELETE, null, Void.class
        );

        //confirm task not found
        assertThat(deleteResponse.getStatusCode()).isEqualTo(NOT_FOUND);
    }


}
