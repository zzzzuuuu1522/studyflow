package com.example.studytask.service;

import com.example.studytask.dto.TaskCreateRequest;
import com.example.studytask.dto.TaskResponse;
import com.example.studytask.entity.Task;
import com.example.studytask.entity.TaskPriority;
import com.example.studytask.entity.TaskStatus;
import com.example.studytask.repository.TaskRepository;
import com.example.studytask.repository.StudyRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, studyRecordRepository);
    }

    @Test
    void usesMediumPriorityWhenCreateRequestOmitsPriority() {
        TaskCreateRequest request = new TaskCreateRequest(
                "学习 JPA",
                null,
                TaskStatus.NOT_STARTED,
                null,
                null
        );
        when(taskRepository.saveAndFlush(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.createTask(request);

        assertThat(response.priority()).isEqualTo(TaskPriority.MEDIUM);
    }

    @Test
    void filtersTasksByPriority() {
        Task highPriorityTask = task(TaskStatus.NOT_STARTED, TaskPriority.HIGH);
        when(taskRepository.findByPriority(TaskPriority.HIGH))
                .thenReturn(List.of(highPriorityTask));

        List<TaskResponse> result = taskService.getTasks(null, TaskPriority.HIGH);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().priority()).isEqualTo(TaskPriority.HIGH);
        verify(taskRepository).findByPriority(TaskPriority.HIGH);
    }

    @Test
    void filtersTasksByStatusAndPriorityTogether() {
        Task matchingTask = task(TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
        when(taskRepository.findByStatusAndPriority(TaskStatus.IN_PROGRESS, TaskPriority.HIGH))
                .thenReturn(List.of(matchingTask));

        List<TaskResponse> result = taskService.getTasks(TaskStatus.IN_PROGRESS, TaskPriority.HIGH);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getFirst().priority()).isEqualTo(TaskPriority.HIGH);
        verify(taskRepository).findByStatusAndPriority(TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
    }

    @Test
    void deletesStudyRecordsBeforeDeletingTask() {
        Task task = task(TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM);
        when(taskRepository.findById(7L)).thenReturn(java.util.Optional.of(task));

        taskService.deleteTask(7L);

        verify(studyRecordRepository).deleteByTaskId(7L);
        verify(taskRepository).delete(task);
    }

    private Task task(TaskStatus status, TaskPriority priority) {
        return new Task("测试任务", null, status, priority, null);
    }
}
