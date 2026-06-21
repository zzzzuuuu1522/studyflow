package com.example.studytask.controller;

import com.example.studytask.dto.StudyRecordRequest;
import com.example.studytask.dto.StudyRecordResponse;
import com.example.studytask.exception.TaskNotFoundException;
import com.example.studytask.service.StudyRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyRecordController.class)
class StudyRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudyRecordService studyRecordService;

    @Test
    void createsStudyRecord() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 6, 21, 10, 0);
        StudyRecordResponse response = new StudyRecordResponse(
                1L, 1L, "Spring Boot", LocalDate.of(2026, 6, 21),
                90, "完成接口练习", now, now
        );
        when(studyRecordService.create(any(StudyRecordRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/study-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskId": 1,
                                  "studyDate": "2026-06-21",
                                  "durationMinutes": 90,
                                  "note": "完成接口练习"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskTitle").value("Spring Boot"))
                .andExpect(jsonPath("$.durationMinutes").value(90));
    }

    @Test
    void returnsNotFoundWhenTaskDoesNotExist() throws Exception {
        when(studyRecordService.create(any(StudyRecordRequest.class)))
                .thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(post("/api/study-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskId": 99,
                                  "studyDate": "2026-06-21",
                                  "durationMinutes": 30
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void rejectsInvalidDurationMinutes() throws Exception {
        mockMvc.perform(post("/api/study-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskId": 1,
                                  "studyDate": "2026-06-21",
                                  "durationMinutes": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("durationMinutes 必须在 1 到 1440 之间"));
    }
}
