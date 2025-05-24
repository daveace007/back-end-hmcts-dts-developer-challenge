package com.richard.task;

import static com.richard.task.constant.Constant.*;
import com.richard.task.validation.ValidStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Table
record Task(
        @Id
        Long id,

        @NotBlank
        @Size(min = 2, max = 100, message = TITLE_SIZE_VALIDATION_MESSAGE)
        @Pattern(regexp = "^[A-Za-z0-9- ]+$", message = TITLE_REGEXP_VALIDATION_MESSAGE)
        String title,

        @Size(min = 0, max = 500, message = DESCRIPTION_SIZE_VALIDATION_MESSAGE)
        @Pattern(regexp = "^[A-Za-z0-9.,!?'\"()\\-:;\\s]+$", message = DESCRIPTION_REGEXP_VALIDATION_MESSAGE)
        String description,

        @ValidStatus
        String status,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @FutureOrPresent
        LocalDateTime dueDateTime
){

}