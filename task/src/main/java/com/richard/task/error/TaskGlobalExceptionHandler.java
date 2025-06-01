package com.richard.task.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TaskGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TaskErrorResponse> handleExceptions(TaskException ex, HttpServletRequest request){
        return createResponse(ex.getMessage(), request, ex.getStatus());
    }

    @ExceptionHandler({NumberFormatException.class, IllegalArgumentException.class})
    public ResponseEntity<TaskErrorResponse> handleException(Exception ex, HttpServletRequest request){
        return createResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TaskErrorResponse> handleException(MethodArgumentNotValidException ex, HttpServletRequest request){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(","));
        return createResponse(message, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<TaskErrorResponse> createResponse(String message, HttpServletRequest request, HttpStatus status){
        TaskErrorResponse errorResponse = new TaskErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(
                errorResponse,
                status
        );
    }

}
