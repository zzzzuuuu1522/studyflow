package com.example.studytask.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String code,
        String path
) {
    public ApiErrorResponse(LocalDateTime timestamp, int status, String message, String path) {
        this(timestamp, status, message, null, path);
    }
}
