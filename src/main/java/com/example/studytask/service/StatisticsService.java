package com.example.studytask.service;

import com.example.studytask.dto.DailyStudyTotalResponse;
import com.example.studytask.dto.DashboardStatisticsResponse;
import com.example.studytask.dto.TaskStudyTotalResponse;
import com.example.studytask.entity.StudyRecord;
import com.example.studytask.exception.BadRequestException;
import com.example.studytask.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final StudyRecordRepository studyRecordRepository;

    public StatisticsService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }

    public DashboardStatisticsResponse getDashboard(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        LocalDate resolvedEndDate = endDate == null ? today : endDate;
        LocalDate resolvedStartDate = startDate == null
                ? resolvedEndDate.minusDays(6)
                : startDate;

        if (resolvedStartDate.isAfter(resolvedEndDate)) {
            throw new BadRequestException("startDate 不能晚于 endDate");
        }

        List<StudyRecord> records = studyRecordRepository.findForStatistics(
                resolvedStartDate,
                resolvedEndDate
        );

        Map<LocalDate, Integer> dailyMap = new TreeMap<>();
        Map<Long, TaskAccumulator> taskMap = new LinkedHashMap<>();
        int periodTotal = 0;

        for (StudyRecord record : records) {
            int minutes = record.getDurationMinutes();
            periodTotal += minutes;
            dailyMap.merge(record.getStudyDate(), minutes, Integer::sum);
            taskMap.computeIfAbsent(
                    record.getTask().getId(),
                    id -> new TaskAccumulator(id, record.getTask().getTitle())
            ).add(minutes);
        }

        List<DailyStudyTotalResponse> dailyTotals = dailyMap.entrySet().stream()
                .map(entry -> new DailyStudyTotalResponse(entry.getKey(), entry.getValue()))
                .toList();

        List<TaskStudyTotalResponse> taskTotals = taskMap.values().stream()
                .map(TaskAccumulator::toResponse)
                .sorted(Comparator.comparing(TaskStudyTotalResponse::durationMinutes).reversed())
                .toList();

        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monthStart = today.withDayOfMonth(1);

        return new DashboardStatisticsResponse(
                resolvedStartDate,
                resolvedEndDate,
                sum(today, today),
                sum(weekStart, today),
                sum(monthStart, today),
                periodTotal,
                (long) records.size(),
                dailyTotals,
                taskTotals
        );
    }

    private int sum(LocalDate startDate, LocalDate endDate) {
        Long total = studyRecordRepository.sumDurationBetween(startDate, endDate);
        return total == null ? 0 : total.intValue();
    }

    private static final class TaskAccumulator {
        private final Long taskId;
        private final String taskTitle;
        private int durationMinutes;

        private TaskAccumulator(Long taskId, String taskTitle) {
            this.taskId = taskId;
            this.taskTitle = taskTitle;
        }

        private void add(int minutes) {
            durationMinutes += minutes;
        }

        private TaskStudyTotalResponse toResponse() {
            return new TaskStudyTotalResponse(taskId, taskTitle, durationMinutes);
        }
    }
}
