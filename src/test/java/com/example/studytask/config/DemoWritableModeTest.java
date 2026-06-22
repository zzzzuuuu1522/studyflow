package com.example.studytask.config;

import com.example.studytask.controller.TaskController;
import com.example.studytask.dto.TaskCreateRequest;
import com.example.studytask.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(WebConfig.class)
@TestPropertySource(properties = "app.demo.read-only=false")
class DemoWritableModeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void allowsWriteRequestsWhenReadOnlyModeIsDisabled() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "测试可写模式",
                                  "status": "NOT_STARTED",
                                  "priority": "MEDIUM"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(TaskCreateRequest.class));
    }
}
