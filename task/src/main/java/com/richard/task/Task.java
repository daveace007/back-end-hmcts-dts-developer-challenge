package com.richard.task;

import static com.richard.task.constant.Constant.*;

import com.richard.task.validation.ValidStatus;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Table
public record Task(
        @Id
        Long id,

        @NotBlank
        @Size(min = 2, max = 100, message = TITLE_SIZE_VALIDATION_MESSAGE)
        @Pattern(regexp = TITLE_REGEXP, message = TITLE_REGEXP_VALIDATION_MESSAGE)
        String title,

        @Size(max = 500, message = DESCRIPTION_SIZE_VALIDATION_MESSAGE)
        @Pattern(regexp = DESCRIPTION_REGEXP, message = DESCRIPTION_REGEXP_VALIDATION_MESSAGE)
        String description,

        @ValidStatus
        String status,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @Future
        LocalDateTime dueDateTime
) {

}