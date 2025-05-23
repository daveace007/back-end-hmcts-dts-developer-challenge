package com.richard.task;

import static com.richard.task.constant.Constant.*;
import com.richard.task.validation.ValidStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

record Task(
        Long id,

        @NotBlank
        @Size(min = 2, max = 100, message = TITLE_SIZE_VALIDATION_MESSAGE)
        @Pattern(regexp = "^[A-Za-z0-9\u00A0]+$", message = TITLE_REGEXP_VALIDATION_MESSAGE)
        String title,

        @Size(min = 0, max = 500, message = DESCRIPTION_SIZE_VALIDATION_MESSAGE)
        @Pattern(regexp = "^[A-Za-z0-9.,!?'\"()\\-:;\\s]+$", message = DESCRIPTION_REGEXP_VALIDATION_MESSAGE)
        String description,

        @ValidStatus
        String status,

        @FutureOrPresent
        LocalDateTime dueDateTime
){

}