package com.example.studytask.dto;

import com.example.studytask.entity.TaskPriority;
import com.example.studytask.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate deadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
