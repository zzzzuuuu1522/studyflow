package com.example.studytask.dto;

import com.example.studytask.entity.TaskPriority;
import com.example.studytask.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskUpdateRequest(
        @NotBlank(message = "title 不能为空")
        @Size(max = 100, message = "title 长度不能超过 100 个字符")
        String title,

        @Size(max = 500, message = "description 长度不能超过 500 个字符")
        String description,

        @NotNull(message = "status 不能为空")
        TaskStatus status,

        @NotNull(message = "priority 不能为空")
        TaskPriority priority,
        LocalDate deadline
) {
}
