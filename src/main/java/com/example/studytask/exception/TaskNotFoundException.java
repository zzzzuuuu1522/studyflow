package com.example.studytask.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("未找到 id 为 " + id + " 的任务");
    }
}
