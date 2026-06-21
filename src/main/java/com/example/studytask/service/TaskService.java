package com.example.studytask.service;

import com.example.studytask.dto.TaskCreateRequest;
import com.example.studytask.dto.TaskResponse;
import com.example.studytask.dto.TaskStatusUpdateRequest;
import com.example.studytask.dto.TaskUpdateRequest;
import com.example.studytask.entity.Task;
import com.example.studytask.entity.TaskPriority;
import com.example.studytask.entity.TaskStatus;
import com.example.studytask.exception.TaskNotFoundException;
import com.example.studytask.repository.TaskRepository;
import com.example.studytask.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final StudyRecordRepository studyRecordRepository;

    public TaskService(TaskRepository taskRepository, StudyRecordRepository studyRecordRepository) {
        this.taskRepository = taskRepository;
        this.studyRecordRepository = studyRecordRepository;
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = new Task(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                request.deadline()
        );
        return toResponse(taskRepository.saveAndFlush(task));
    }

    public List<TaskResponse> getTasks(TaskStatus status, TaskPriority priority) {
        List<Task> tasks;
        if (status != null && priority != null) {
            tasks = taskRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status);
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(priority);
        } else {
            tasks = taskRepository.findAll();
        }
        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponse getTask(Long id) {
        return toResponse(findTask(id));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = findTask(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDeadline(request.deadline());
        return toResponse(taskRepository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = findTask(id);
        task.setStatus(request.status());
        return toResponse(taskRepository.saveAndFlush(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = findTask(id);
        studyRecordRepository.deleteByTaskId(id);
        taskRepository.delete(task);
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDeadline(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
