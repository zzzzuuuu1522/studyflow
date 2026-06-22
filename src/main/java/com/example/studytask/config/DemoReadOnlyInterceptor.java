package com.example.studytask.config;

import com.example.studytask.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

public class DemoReadOnlyInterceptor implements HandlerInterceptor {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final String ERROR_MESSAGE = "当前为公开演示只读模式，暂不支持修改数据。";
    private static final String ERROR_CODE = "DEMO_READ_ONLY";

    private final boolean readOnly;
    private final ObjectMapper objectMapper;

    public DemoReadOnlyInterceptor(boolean readOnly, ObjectMapper objectMapper) {
        this.readOnly = readOnly;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {
        if (!readOnly || !WRITE_METHODS.contains(request.getMethod())) {
            return true;
        }

        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpServletResponse.SC_FORBIDDEN,
                ERROR_MESSAGE,
                ERROR_CODE,
                request.getRequestURI()
        );
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
        return false;
    }
}
