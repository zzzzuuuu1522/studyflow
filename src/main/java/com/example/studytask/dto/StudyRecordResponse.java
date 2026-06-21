package com.example.studytask.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudyRecordResponse(
        Long id,
        Long taskId,
        String taskTitle,
        LocalDate studyDate,
        Integer durationMinutes,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
