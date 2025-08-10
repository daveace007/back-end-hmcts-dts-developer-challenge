package com.richard.task;

import com.richard.task.constant.Constant;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import static com.richard.task.constant.Constant.URL_REGEX_PATTERN;

@Table
public record Origin(
        @Id
        Long id,
        @Pattern(regexp = URL_REGEX_PATTERN, message ="Invalid url")
        String uri
) {
        }
