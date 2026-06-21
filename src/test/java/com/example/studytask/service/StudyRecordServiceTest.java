package com.example.studytask.service;

import com.example.studytask.dto.StudyRecordRequest;
import com.example.studytask.dto.StudyRecordResponse;
import com.example.studytask.entity.StudyRecord;
import com.example.studytask.entity.Task;
import com.example.studytask.exception.TaskNotFoundException;
import com.example.studytask.repository.StudyRecordRepository;
import com.example.studytask.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyRecordServiceTest {

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @Mock
    private TaskRepository taskRepository;

    private StudyRecordService studyRecordService;

    @BeforeEach
    void setUp() {
        studyRecordService = new StudyRecordService(studyRecordRepository, taskRepository);
    }

    @Test
    void createsStudyRecord() {
        Task task = task(1L, "Spring Boot");
        StudyRecordRequest request = new StudyRecordRequest(
                1L,
                LocalDate.of(2026, 6, 21),
                90,
                "完成接口练习"
        );
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(studyRecordRepository.saveAndFlush(any(StudyRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StudyRecordResponse response = studyRecordService.create(request);

        assertThat(response.taskId()).isEqualTo(1L);
        assertThat(response.taskTitle()).isEqualTo("Spring Boot");
        assertThat(response.durationMinutes()).isEqualTo(90);
    }

    @Test
    void throwsNotFoundWhenCreatingForMissingTask() {
        StudyRecordRequest request = new StudyRecordRequest(
                99L,
                LocalDate.of(2026, 6, 21),
                45,
                null
        );
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyRecordService.create(request))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void queriesRecordsByDateRange() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        when(studyRecordRepository.search(null, startDate, endDate)).thenReturn(List.of());

        List<StudyRecordResponse> result = studyRecordService.getRecords(null, startDate, endDate);

        assertThat(result).isEmpty();
        verify(studyRecordRepository).search(null, startDate, endDate);
    }

    private Task task(Long id, String title) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getTitle()).thenReturn(title);
        return task;
    }
}
