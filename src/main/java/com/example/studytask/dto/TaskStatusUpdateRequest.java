package com.example.studytask.dto;

import com.example.studytask.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull(message = "status 不能为空")
        TaskStatus status
) {
}
