package com.richard.task.validation;


import static com.richard.task.constant.Constant.STATUS_VALIDATION_MESSAGE;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StatusValidator.class)
public @interface ValidStatus{

    String message() default STATUS_VALIDATION_MESSAGE;

    Class<?> [] groups() default {};

    Class<? extends Payload> [] payload() default {};

}