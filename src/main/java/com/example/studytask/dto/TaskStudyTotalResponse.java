package com.example.studytask.dto;

public record TaskStudyTotalResponse(
        Long taskId,
        String taskTitle,
        Integer durationMinutes
) {
}
