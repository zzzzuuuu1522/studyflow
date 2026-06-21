package com.example.studytask.dto;

import com.example.studytask.entity.TaskPriority;
import com.example.studytask.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank(message = "title 不能为空")
        @Size(max = 100, message = "title 长度不能超过 100 个字符")
        String title,

        @Size(max = 500, message = "description 长度不能超过 500 个字符")
        String description,

        TaskStatus status,
        TaskPriority priority,
        LocalDate deadline
) {
}
