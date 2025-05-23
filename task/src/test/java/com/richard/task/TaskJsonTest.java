package com.richard.task;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;



@JsonTest
class TaskJsonTest{

    @Autowired
    private JacksonTester<Task> json;

    @Autowired
    private JacksonTester<Task[]> jsonList;

    private Task [] tasks;

    @BeforeEach
    void setUp(){
        //initialize an array of tasks
        tasks = Arrays.array(
            new Task(1L, "Back-end Task","Develop and test back-end task", "undone", LocalDateTime.of(2025, 5, 23, 13, 48, 30)),
            new Task(2L, "Front-end Task","Develop front-end task", "undone", LocalDateTime.of(2025, 5, 23, 13, 50, 30))
        );
    }

    @Test
    void taskSerializationTest() throws IOException{
        Task task = tasks[0];
        assertThat(json.write(task)).isStrictlyEqualToJson("task.json");
        assertThat(json.write(task)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(task)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(task)).hasJsonPathStringValue("@.title");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.title").isEqualTo("Back-end Task");
        assertThat(json.write(task)).hasJsonPathStringValue("@.description");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.description").isEqualTo("Develop and test back-end task");
        assertThat(json.write(task)).hasJsonPathStringValue("@.status");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.status").isEqualTo("undone");
        assertThat(json.write(task)).hasJsonPathStringValue("@.dueDateTime");
        assertThat(json.write(task)).extractingJsonPathStringValue("@.dueDateTime").isEqualTo("2025-05-23T13:48:30");
    }


    @Test
    void taskDeserializationTest() throws IOException{
        String expected = """
                {
                    "id": 1,
                    "title": "Back-end Task",
                    "description": "Develop and test back-end task",
                    "status": "undone",
                    "dueDateTime": "2025-05-23T13:48:30"
                }
                """;

        assertThat(json.parse(expected))
                .isEqualTo(new Task(1L, "Back-end Task","Develop and test back-end task", "undone", LocalDateTime.of(2025, 5, 23, 13, 48, 30)));
        assertThat(json.parseObject(expected).id()).isEqualTo(1L);
        assertThat(json.parseObject(expected).title()).isEqualTo("Back-end Task");
        assertThat(json.parseObject(expected).description()).isEqualTo("Develop and test back-end task");
        assertThat(json.parseObject(expected).status()).isEqualTo("undone");
        assertThat(json.parseObject(expected).dueDateTime()).isEqualTo("2025-05-23T13:48:30");
    }

    @Test
    void taskListSerializationTest() throws IOException{
        assertThat(jsonList.write(tasks)).isStrictlyEqualToJson("tasks.json");
    }

    @Test
    void taskListDeserializationTest() throws IOException{
        String expected = """
                [
                  {
                  "id": 1,
                  "title": "Back-end Task",
                  "description": "Develop and test back-end task",
                  "status": "undone",
                  "dueDateTime": "2025-05-23T13:48:30"
                 },
                 {
                  "id": 2,
                  "title": "Front-end Task",
                  "description": "Develop front-end task",
                  "status": "undone",
                  "dueDateTime": "2025-05-23T13:50:30"
                  }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(tasks);
    }

}

