package com.richard.task.validation;

import com.richard.task.Status;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.Stream;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && Stream
                .of(Status.values())
                .map(Status::label)
                .anyMatch(label->label.equalsIgnoreCase(value));
    }
}
