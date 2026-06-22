package com.example.studytask.config;

import com.example.studytask.controller.TaskController;
import com.example.studytask.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(WebConfig.class)
@TestPropertySource(properties = {
        "app.demo.read-only=true",
        "app.cors.allowed-origin=http://localhost:5173"
})
class DemoReadOnlyModeTest {

    private static final String ORIGIN = "http://localhost:5173";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void allowsGetRequests() throws Exception {
        when(taskService.getTasks(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks").header("Origin", ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN));
    }

    @Test
    void blocksPostRequests() throws Exception {
        assertReadOnly(post("/api/tasks"), "/api/tasks");
    }

    @Test
    void blocksPutRequests() throws Exception {
        assertReadOnly(put("/api/tasks/1"), "/api/tasks/1");
    }

    @Test
    void blocksPatchRequests() throws Exception {
        assertReadOnly(patch("/api/tasks/1/status"), "/api/tasks/1/status");
    }

    @Test
    void blocksDeleteRequests() throws Exception {
        assertReadOnly(delete("/api/tasks/1"), "/api/tasks/1");
    }

    private void assertReadOnly(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            String path) throws Exception {
        mockMvc.perform(request.header("Origin", ORIGIN))
                .andExpect(status().isForbidden())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("当前为公开演示只读模式，暂不支持修改数据。"))
                .andExpect(jsonPath("$.code").value("DEMO_READ_ONLY"))
                .andExpect(jsonPath("$.path").value(path));
    }
}
