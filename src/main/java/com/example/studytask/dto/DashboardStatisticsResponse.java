package com.example.studytask.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardStatisticsResponse(
        LocalDate startDate,
        LocalDate endDate,
        Integer todayMinutes,
        Integer currentWeekMinutes,
        Integer currentMonthMinutes,
        Integer periodTotalMinutes,
        Long recordCount,
        List<DailyStudyTotalResponse> dailyTotals,
        List<TaskStudyTotalResponse> taskTotals
) {
}
