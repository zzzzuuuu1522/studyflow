package com.example.studytask.dto;

import java.time.LocalDate;

public record DailyStudyTotalResponse(
        LocalDate date,
        Integer durationMinutes
) {
}
