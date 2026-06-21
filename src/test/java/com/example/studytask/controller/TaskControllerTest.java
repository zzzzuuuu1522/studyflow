package com.example.studytask.controller;

import com.example.studytask.dto.TaskCreateRequest;
import com.example.studytask.dto.TaskResponse;
import com.example.studytask.dto.TaskStatusUpdateRequest;
import com.example.studytask.dto.TaskUpdateRequest;
import com.example.studytask.entity.TaskPriority;
import com.example.studytask.entity.TaskStatus;
import com.example.studytask.exception.TaskNotFoundException;
import com.example.studytask.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createsTask() throws Exception {
        TaskResponse response = taskResponse(1L, TaskStatus.NOT_STARTED);
        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "学习 Spring Boot",
                                  "description": "完成任务管理 API",
                                  "deadline": "2026-07-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    @Test
    void filtersTasksByStatus() throws Exception {
        when(taskService.getTasks(TaskStatus.IN_PROGRESS, null))
                .thenReturn(List.of(taskResponse(2L, TaskStatus.IN_PROGRESS)));

        mockMvc.perform(get("/api/tasks").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }

    @Test
    void returnsNotFoundForMissingTask() throws Exception {
        when(taskService.getTask(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("未找到 id 为 99 的任务"))
                .andExpect(jsonPath("$.path").value("/api/tasks/99"));
    }

    @Test
    void updatesTaskStatus() throws Exception {
        when(taskService.updateStatus(any(Long.class), any(TaskStatusUpdateRequest.class)))
                .thenReturn(taskResponse(1L, TaskStatus.COMPLETED));

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updatesTaskWithHighPriority() throws Exception {
        when(taskService.updateTask(any(Long.class), any(TaskUpdateRequest.class)))
                .thenReturn(taskResponse(1L, TaskStatus.IN_PROGRESS, TaskPriority.HIGH));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "深入学习 Spring Boot",
                                  "description": "检查完整更新请求",
                                  "status": "IN_PROGRESS",
                                  "priority": "HIGH",
                                  "deadline": "2026-06-21"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"));

        ArgumentCaptor<TaskUpdateRequest> requestCaptor = ArgumentCaptor.forClass(TaskUpdateRequest.class);
        verify(taskService).updateTask(any(Long.class), requestCaptor.capture());
        assertThat(requestCaptor.getValue().priority()).isEqualTo(TaskPriority.HIGH);
    }

    @Test
    void deletesTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    private TaskResponse taskResponse(Long id, TaskStatus status) {
        return taskResponse(id, status, TaskPriority.MEDIUM);
    }

    private TaskResponse taskResponse(Long id, TaskStatus status, TaskPriority priority) {
        LocalDateTime now = LocalDateTime.of(2026, 6, 21, 10, 0);
        return new TaskResponse(
                id,
                "学习 Spring Boot",
                "完成任务管理 API",
                status,
                priority,
                LocalDate.of(2026, 7, 1),
                now,
                now
        );
    }
}
