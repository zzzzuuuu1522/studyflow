package com.example.studytask.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudyRecordRequest(
        @NotNull(message = "taskId 不能为空")
        Long taskId,

        @NotNull(message = "studyDate 不能为空")
        LocalDate studyDate,

        @NotNull(message = "durationMinutes 不能为空")
        @Min(value = 1, message = "durationMinutes 必须在 1 到 1440 之间")
        @Max(value = 1440, message = "durationMinutes 必须在 1 到 1440 之间")
        Integer durationMinutes,

        @Size(max = 500, message = "note 长度不能超过 500 个字符")
        String note
) {
}
