package com.example.studytask.controller;

import com.example.studytask.dto.StudyRecordRequest;
import com.example.studytask.dto.StudyRecordResponse;
import com.example.studytask.service.StudyRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {

    private final StudyRecordService studyRecordService;

    public StudyRecordController(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyRecordResponse create(@Valid @RequestBody StudyRecordRequest request) {
        return studyRecordService.create(request);
    }

    @GetMapping
    public List<StudyRecordResponse> getRecords(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return studyRecordService.getRecords(taskId, startDate, endDate);
    }

    @PutMapping("/{id}")
    public StudyRecordResponse update(
            @PathVariable Long id,
            @Valid @RequestBody StudyRecordRequest request) {
        return studyRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studyRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
