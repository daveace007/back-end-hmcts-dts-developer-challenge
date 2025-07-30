package com.richard.task.constant;

public interface Constant {


    String TITLE_SIZE_VALIDATION_MESSAGE = "Title must be between 2 and 100 characters long";
    String TITLE_REGEXP_VALIDATION_MESSAGE = "Title must contain only alphanumerics and spaces";
    String DESCRIPTION_SIZE_VALIDATION_MESSAGE = "Description must be between 0 and 1500 characters";
    String DESCRIPTION_REGEXP_VALIDATION_MESSAGE = "Description contains invalid characters";
    String STATUS_VALIDATION_MESSAGE = "Invalid status. Status must be To do, Completed, Cancelled, On Hold, Deferred, In Progress, Pending, Failed or Reviewing";
    String TITLE_REGEXP = "^[A-Za-z0-9- ]+$";
    String DESCRIPTION_REGEXP = "^[A-Za-z0-9.,!?'\"()\\-:;\\s]+$";
}
