package com.example.studytask.service;

import com.example.studytask.dto.DashboardStatisticsResponse;
import com.example.studytask.entity.StudyRecord;
import com.example.studytask.entity.Task;
import com.example.studytask.repository.StudyRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StudyRecordRepository studyRecordRepository;

    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsService(studyRecordRepository);
        when(studyRecordRepository.sumDurationBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(0L);
    }

    @Test
    void aggregatesMinutesByDateAndTask() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        Task springTask = task(1L, "Spring Boot");
        Task vueTask = task(2L, "Vue 3");
        List<StudyRecord> records = List.of(
                record(springTask, LocalDate.of(2026, 6, 20), 60),
                record(springTask, LocalDate.of(2026, 6, 20), 30),
                record(vueTask, LocalDate.of(2026, 6, 21), 45)
        );
        when(studyRecordRepository.findForStatistics(startDate, endDate)).thenReturn(records);

        DashboardStatisticsResponse response = statisticsService.getDashboard(startDate, endDate);

        assertThat(response.periodTotalMinutes()).isEqualTo(135);
        assertThat(response.recordCount()).isEqualTo(3);
        assertThat(response.dailyTotals()).hasSize(2);
        assertThat(response.dailyTotals().getFirst().durationMinutes()).isEqualTo(90);
        assertThat(response.taskTotals().getFirst().taskTitle()).isEqualTo("Spring Boot");
        assertThat(response.taskTotals().getFirst().durationMinutes()).isEqualTo(90);
    }

    @Test
    void returnsZerosAndEmptyListsWhenThereAreNoRecords() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        when(studyRecordRepository.findForStatistics(startDate, endDate)).thenReturn(List.of());

        DashboardStatisticsResponse response = statisticsService.getDashboard(startDate, endDate);

        assertThat(response.periodTotalMinutes()).isZero();
        assertThat(response.recordCount()).isZero();
        assertThat(response.todayMinutes()).isZero();
        assertThat(response.dailyTotals()).isEmpty();
        assertThat(response.taskTotals()).isEmpty();
    }

    private StudyRecord record(Task task, LocalDate date, int minutes) {
        return new StudyRecord(task, date, minutes, null);
    }

    private Task task(Long id, String title) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getTitle()).thenReturn(title);
        return task;
    }
}
