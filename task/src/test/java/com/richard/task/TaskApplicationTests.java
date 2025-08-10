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
import java.util.List;

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
    TaskController taskController;

    @Autowired
    OriginController originController;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    OriginRepository originRepository;


    @Test
    void contextLoads() {
        assertThat(application).isNotNull();
        assertThat(taskController).isNotNull();
        assertThat(originController).isNotNull();
        assertThat(taskRepository).isNotNull();
        assertThat(originRepository).isNotNull();
    }

    @Test
    void shouldCreateANewTask() {
        Task task = new Task(
                null,
                "Back-end Unit Test Task",
                "Test back-end API",
                IN_PROGRESS.label(),
                LocalDateTime.of(3000, 5, 23, 13, 48, 30)
        );

        ResponseEntity<Void> createResponse = template.postForEntity("/api/tasks", task, Void.class);
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
        assertThat(title).isEqualTo("Back-end Unit Test Task");
        assertThat(description).isEqualTo("Test back-end API");
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
        ResponseEntity<Void> createResponse = template.exchange("/api/tasks", POST, createRequest, Void.class);
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
        ResponseEntity<Void> createResponse = template.exchange("/api/tasks", POST, createRequest, Void.class);
        //confirm bad request (400) status code
        assertThat(createResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldNotCreateANewTaskWithAnExistingTitle(){
        // define a task with an already existing title
        Task task = new Task(
                null,
                "Draft Marketing Strategy",
                "This may result in a conflict",
                TODO.label(), LocalDateTime.of(3000, 6, 1, 20, 20, 20)
        );
        // make a post request
        ResponseEntity<Void> response = template.postForEntity("/api/tasks", task, Void.class);
        // confirm bad conflict (409) request
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
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
        ResponseEntity<Void> createResponse = template.exchange("/api/tasks", POST, createRequest, Void.class);
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
        ResponseEntity<Void> createResponse = template.exchange("/api/tasks", POST, createRequest, Void.class);
        //confirm bad request (400) status code
        assertThat(createResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }


    @Test
    void shouldReturnATask() {
        ResponseEntity<String> response = template.getForEntity("/api/tasks/2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        String title = context.read("$.title");
        String description = context.read("$.description");
        String status = context.read("$.status");
        String dueDateTime = context.read("$.dueDateTime");

        assertThat(id).isEqualTo(2);
        assertThat(title).isEqualTo("Optimize Database Indexes");
        assertThat(description).isEqualTo("Analyze current SQL performance logs and update inefficient indexes for large tables.");
        assertThat(status).isEqualTo("In Progress");
        assertThat(dueDateTime).isEqualTo("2025-11-27T13:33:35");
    }

    @Test
    void shouldReturnATaskByTitle(){
        ResponseEntity<String> response = template.getForEntity(
                "/api/tasks/search-title?title=Draft Marketing Strategy",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.content[0].id");
        String title = context.read("$.content[0].title");
        String description = context.read("$.content[0].description");
        String status = context.read("$.content[0].status");
        String dueDateTime = context.read("$.content[0].dueDateTime");

        assertThat(id).isNotNull();
        assertThat(id).isNotEqualTo(0);
        assertThat(title).isEqualTo("Draft Marketing Strategy");
        assertThat(description).isEqualTo("Outline quarterly marketing goals, audience segments, campaign themes, and KPIs to guide the digital team.");
        assertThat(status).isEqualTo(DEFERRED.label());
        assertThat(dueDateTime).isEqualTo("2025-11-23T20:33:35");
    }

    @Test
    void shouldReturnATaskByStatus(){
        ResponseEntity<String> response = template.getForEntity(
                "/api/tasks/search-status?status=Deferred",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        String status = context.read("$.content[0].status");
        assertThat(status).isEqualTo(DEFERRED.label());

    }

    @Test
    void shouldNotReturnATaskWithAnUnknownId() {
        ResponseEntity<String> response = template.getForEntity("/api/task/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnAllTasks() {
        ResponseEntity<String> response = template.getForEntity("/api/tasks", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        int tasksCount = context.read("$.content.length()");
        assertThat(tasksCount).isEqualTo(20);
        JSONArray ids = context.read("$..id");
        JSONArray titles = context.read("$..title");
        JSONArray descriptions = context.read("$..description");
        JSONArray statuses = context.read("$..status");
        JSONArray dueDateTimes = context.read("$..dueDateTime");

        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        assertThat(titles).contains("Draft Marketing Strategy", "Optimize Database Indexes");
        assertThat(descriptions).contains("Prepare a test matrix for core UI workflows including accessibility and cross-browser checks.");
        assertThat(statuses).contains("To do", "In Progress");
        assertThat(dueDateTimes).contains("2025-11-27T13:33:35", "2025-11-23T20:33:35");
    }

    @Test
    void shouldReturnAPageOfTasks() {
        ResponseEntity<String> response = template
                .getForEntity("/api/tasks?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        JSONArray page = context.read("$.content[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfTask() {
        ResponseEntity<String> response = template
                .getForEntity("/api/tasks?page=0&size=1&sort=dueDateTime,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        JSONArray page = context.read("$.content[*]");
        assertThat(page.size()).isEqualTo(1);
        System.out.print(page);

        String dueDateTime = context.read("$.content[0].dueDateTime");
        assertThat(dueDateTime).isEqualTo("2025-12-29T09:50:00");
    }

    @Test
    void shouldReturnASortedPageOfTaskWithNoParametersButDefaultValues() {
        ResponseEntity<String> response = template
                .getForEntity("/api/tasks", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        JSONArray page = context.read("$.content[*]");
        assertThat(page.size()).isEqualTo(20);

        JSONArray ids = context.read("$..id");
        assertThat(ids).containsExactly(1, 2, 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
    }

    @Test
    void shouldUpdateExistingTask() {
        //create a task for update
        Task taskUpdate = new Task(null, "Front-end Task", "Develop Front-end task", Status.COMPLETED.label(), LocalDateTime.of(3000, 5, 23, 13, 30, 30));
        //request an update
        HttpEntity<Task> request = new HttpEntity<>(taskUpdate);
        //request the  updated task
        ResponseEntity<Void> updateResponse = template.exchange("/api/tasks/2", PUT, request, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(NO_CONTENT);

        //verify update operation
        ResponseEntity<String> getResponse = template.getForEntity("/api/tasks/2", String.class);
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
        ResponseEntity<Void> response = template.exchange("/api/tasks/99999", PUT, request, Void.class);

        //confirm that task is non-existent
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldDeleteTask() {
        //create a delete request
        ResponseEntity<Void> deleteResponse = template.exchange(
                "/api/tasks/1", DELETE, null, Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);
        //Get the deleted task
        ResponseEntity<String> getResponse = template.getForEntity("/api/task/1", String.class);
        //confirm that deleted task is not found
        assertThat(getResponse.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotDeleteATaskThatDoesNotExist() {
        // create a delete request
        ResponseEntity<Void> deleteResponse = template.exchange(
                "/api/tasks/999999", DELETE, null, Void.class
        );

        //confirm task not found
        assertThat(deleteResponse.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldCreateNewOrigin(){
        Origin origin = new Origin(null, "http://127.0.0.1:3000");
        ResponseEntity<Void> createResponse = template.postForEntity("/api/origins", origin, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);

    }

    @Test
    void shouldReturnAnOrigin(){
        ResponseEntity<String> response = template.getForEntity("/api/origins/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        String uri = context.read("$.uri");

        assertThat(id).isEqualTo(1);
        assertThat(uri).isEqualTo("http://localhost:3000");
    }

    @Test
    void shouldReturnAllOrigins(){
        ResponseEntity<String> response = template.getForEntity("/api/origins", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        int originCount = context.read("$.length()");
        assertThat(originCount).isEqualTo(1);
        JSONArray ids  = context.read("$..id");
        JSONArray uris = context.read("$..uri");

        assertThat(ids).containsExactlyInAnyOrder(1);
        assertThat(uris).contains("http://localhost:3000");

    }

    @Test
    void shouldUpdateExistingOrigin(){
        Origin originUpdate = new Origin(null, "http://127.0.0.1:3000");

        HttpEntity<Origin> request = new HttpEntity<>(originUpdate);

        ResponseEntity<Void> updateResponse = template.exchange("/api/origins/1", PUT, request, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(NO_CONTENT);

        ResponseEntity<String> getResponse = template.getForEntity("/api/origins/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(OK);

        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        String uri = context.read("$.uri");

        assertThat(id).isEqualTo(1);
        assertThat(uri).isEqualTo("http://127.0.0.1:3000");
    }


    @Test
    void shouldDeleteOrigin(){
        //create a delete request
        ResponseEntity<Void> deleteResponse = template.exchange(
                "/api/origins/1", DELETE, null, Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(NO_CONTENT);
        //Get the deleted task
        ResponseEntity<String> getResponse = template.getForEntity("/api/origins/1", String.class);
        //confirm that deleted task is not found
        assertThat(getResponse.getStatusCode()).isEqualTo(NOT_FOUND);
    }

}
