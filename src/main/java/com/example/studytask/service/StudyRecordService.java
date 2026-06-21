package com.example.studytask.service;

import com.example.studytask.dto.StudyRecordRequest;
import com.example.studytask.dto.StudyRecordResponse;
import com.example.studytask.entity.StudyRecord;
import com.example.studytask.entity.Task;
import com.example.studytask.exception.BadRequestException;
import com.example.studytask.exception.StudyRecordNotFoundException;
import com.example.studytask.exception.TaskNotFoundException;
import com.example.studytask.repository.StudyRecordRepository;
import com.example.studytask.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudyRecordService {

    private final StudyRecordRepository studyRecordRepository;
    private final TaskRepository taskRepository;

    public StudyRecordService(StudyRecordRepository studyRecordRepository, TaskRepository taskRepository) {
        this.studyRecordRepository = studyRecordRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public StudyRecordResponse create(StudyRecordRequest request) {
        Task task = findTask(request.taskId());
        StudyRecord record = new StudyRecord(
                task,
                request.studyDate(),
                request.durationMinutes(),
                request.note()
        );
        return toResponse(studyRecordRepository.saveAndFlush(record));
    }

    public List<StudyRecordResponse> getRecords(Long taskId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return studyRecordRepository.search(taskId, startDate, endDate).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StudyRecordResponse update(Long id, StudyRecordRequest request) {
        StudyRecord record = findRecord(id);
        Task task = findTask(request.taskId());
        record.setTask(task);
        record.setStudyDate(request.studyDate());
        record.setDurationMinutes(request.durationMinutes());
        record.setNote(request.note());
        return toResponse(studyRecordRepository.saveAndFlush(record));
    }

    @Transactional
    public void delete(Long id) {
        studyRecordRepository.delete(findRecord(id));
    }

    private Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private StudyRecord findRecord(Long id) {
        return studyRecordRepository.findById(id)
                .orElseThrow(() -> new StudyRecordNotFoundException(id));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate 不能晚于 endDate");
        }
    }

    private StudyRecordResponse toResponse(StudyRecord record) {
        return new StudyRecordResponse(
                record.getId(),
                record.getTask().getId(),
                record.getTask().getTitle(),
                record.getStudyDate(),
                record.getDurationMinutes(),
                record.getNote(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
