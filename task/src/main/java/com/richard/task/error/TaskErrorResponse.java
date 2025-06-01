package com.richard.task.error;

import java.time.LocalDateTime;

public record TaskErrorResponse(LocalDateTime time, int status, String Message, String path){}

