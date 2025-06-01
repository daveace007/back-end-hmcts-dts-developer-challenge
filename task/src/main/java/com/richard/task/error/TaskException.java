package com.richard.task.error;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class TaskException extends Exception{

    private static final String MESSAGE_NOT_NULL = "Message must not be null";

    private static final String STATUS_NOT_NULL = "Status must not be null";

    private HttpStatus status;

    public TaskException(String message, HttpStatus status){
        super(Objects.requireNonNull(message, MESSAGE_NOT_NULL));
        this.status = checkStatusForNull(status);
    }

    public TaskException(HttpStatus status){
        super(checkStatusForNull(status).getReasonPhrase());
        this.status = status;
    }

    public void setStatus(HttpStatus status){
        this.status = checkStatusForNull(status);
    }

    public HttpStatus getStatus(){
        return status;
    }

    private static HttpStatus checkStatusForNull(HttpStatus status) {
        return Objects.requireNonNull(status, STATUS_NOT_NULL);
    }
}

