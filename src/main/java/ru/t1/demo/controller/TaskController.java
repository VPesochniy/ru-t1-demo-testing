package ru.t1.demo.controller;

import org.springframework.web.bind.annotation.*;
import ru.t1.demo.dto.TaskDto;
import ru.t1.demo.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public TaskDto saveTask(@RequestBody TaskDto dto) {
        return taskService.saveTask(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable UUID id, @RequestBody TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

}
