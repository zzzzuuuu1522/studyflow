package com.example.studytask.exception;

public class StudyRecordNotFoundException extends RuntimeException {

    public StudyRecordNotFoundException(Long id) {
        super("未找到 id 为 " + id + " 的学习记录");
    }
}
