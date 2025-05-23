package com.richard.task;

import java.time.LocalDateTime;

record Task(Long id, String title, String description, String status, LocalDateTime dueDateTime){
    
}