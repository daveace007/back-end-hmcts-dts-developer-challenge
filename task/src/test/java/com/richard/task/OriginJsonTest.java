package com.richard.task;


import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class OriginJsonTest {

    @Autowired
    private JacksonTester<Origin> json;

    @Autowired
    private JacksonTester<Origin[]> jsonList;

    private Origin[] origins;

    @BeforeEach
    void setup() {
        //initialize an array of origins
        origins = Arrays.array(
                new Origin(1L, "http://127.0.0.1:3000"),
                new Origin(2L, "http://192.168.0.15:3000"),
                new Origin(3L, "http://192.168.40.6:3000"),
                new Origin(4L, "http://localhost:3000")

        );
    }

    @Test
    void originSerializationTest() throws IOException {
        Origin origin = origins[0];
        assertThat(json.write(origin)).isStrictlyEqualToJson("origin.json");
        assertThat(json.write(origin)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(origin)).hasJsonPathStringValue("@.uri");
    }

    @Test
    void originUriTest() throws IOException {
        String expected = """
                {
                    "id":1,
                    "uri":"http://127.0.0.1:3000"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new Origin(1L, "http://127.0.0.1:3000"));
        assertThat(json.parseObject(expected).id()).isEqualTo(1L);
        assertThat(json.parseObject(expected).uri()).isEqualTo("http://127.0.0.1:3000");
    }

    @Test
    void originListSerializationTest() throws IOException {
        assertThat(jsonList.write(origins)).isStrictlyEqualToJson("origins.json");
    }

    @Test
    void originListDeserializationTest() throws IOException {
        String expected = """
                    [
                       {
                         "id": 1,
                         "uri": "http://127.0.0.1:3000"
                       },
                       {
                         "id": 2,
                         "uri": "http://192.168.0.15:3000"
                       },
                       {
                         "id": 3,
                         "uri": "http://192.168.40.6:3000"
                       },
                       {
                         "id": 4,
                         "uri": "http://localhost:3000"
                       }
                    ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(origins);
    }
}
