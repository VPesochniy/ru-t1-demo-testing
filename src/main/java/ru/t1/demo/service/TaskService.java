package ru.t1.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.demo.dto.TaskDto;
import ru.t1.demo.entity.Task;
import ru.t1.demo.exception.TaskIsPresentException;
import ru.t1.demo.exception.TaskNotFoundException;
import ru.t1.demo.repository.TaskRepository;
import ru.t1.demo.util.TaskMapper;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    public TaskDto getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id" + id + " not found"));

        return TaskMapper.toDto(task);
    }

    public TaskDto saveTask(TaskDto dto) {

        if (taskRepository.findTaskByTitle(dto.title()).isPresent()) {
            throw new TaskIsPresentException("Task is already present");
        }

        Task taskToSave = TaskMapper.toEntity(dto);
        Task savedTask = taskRepository.save(taskToSave);

        return TaskMapper.toDto(savedTask);
    }

    public void deleteTaskById(UUID id) {
        TaskDto taskDto = getTaskById(id);
        Task taskToDelete = TaskMapper.toEntity(taskDto);

        taskRepository.delete(taskToDelete);
    }

    @Transactional
    public TaskDto updateTask(UUID id, TaskDto updateTaskRequest) {

        TaskDto existingTaskDto = getTaskById(id);
        Task existingTask = TaskMapper.toEntity(existingTaskDto);

        if (updateTaskRequest.title() != null && !Objects.equals(updateTaskRequest.title(), existingTaskDto.title())) {
            existingTask.setTitle(updateTaskRequest.title());
        }

        if (updateTaskRequest.description() != null && !Objects.equals(updateTaskRequest.description(), existingTaskDto.description())) {
            existingTask.setDescription(updateTaskRequest.description());
        }

        if (updateTaskRequest.status() != null && !Objects.equals(updateTaskRequest.status(), existingTaskDto.status())) {
            existingTask.setStatus(updateTaskRequest.status());
        }

        return TaskMapper.toDto(existingTask);
    }
}
