package ru.t1.demo.service;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.demo.dto.TaskDto;
import ru.t1.demo.entity.Task;
import ru.t1.demo.entity.TaskStatus;
import ru.t1.demo.exception.TaskIsPresentException;
import ru.t1.demo.exception.TaskNotFoundException;
import ru.t1.demo.repository.TaskRepository;
import ru.t1.demo.util.TaskMapper;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;

    TaskDto taskDto;
    Task task;
    List<TaskDto> randomTasksDto;
    List<Task> randomTasks;

    @BeforeEach
    void setUp() {
        Faker faker = new Faker();
        Random random = new Random();
        randomTasks = new ArrayList<>();
        randomTasksDto = new ArrayList<>();

        taskDto = TaskDto.builder()
                .id(UUID.randomUUID())
                .title("test task")
                .description("test description")
                .status(TaskStatus.NOT_STARTED)
                .build();

        task = TaskMapper.toEntity(taskDto);

        IntStream.range(0, 10).forEach(i -> {
            int randomTaskStatus = random.nextInt(TaskStatus.values().length);
            TaskDto dto = TaskDto.builder()
                    .id(UUID.randomUUID())
                    .title(faker.text().text(5, 25, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE))
                    .description(faker.text().text(0, 255, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE))
                    .status(TaskStatus.values()[randomTaskStatus])
                    .build();

            randomTasksDto.add(dto);
        });

        randomTasks = randomTasksDto.stream()
                .map(TaskMapper::toEntity)
                .toList();
    }

    @Test
    void getAllTask_OneEntity() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskDto> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals(taskDto, tasks.get(0));
    }

    @Test
    void getAllTask_MultipleEntity() {
        when(taskRepository.findAll()).thenReturn(randomTasks);

        List<TaskDto> tasks = taskService.getAllTasks();

        assertEquals(randomTasksDto.size(), tasks.size());
        assertEquals(randomTasksDto.get(0), tasks.get(0));
    }

    @Test
    void getTaskById_TaskExists() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.of(task));

        TaskDto foundTask = taskService.getTaskById(taskDto.id());

        assertEquals(taskDto, foundTask);
    }

    @Test
    void getTaskById_TaskNotFound() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskDto.id()));
    }

    @Test
    void saveTask_TaskIsPresent() {
        when(taskRepository.findTaskByTitle(taskDto.title())).thenReturn(Optional.of(task));

        assertThrows(TaskIsPresentException.class, () -> taskService.saveTask(taskDto));
    }

    @Test
    void saveTask_Success() {
        when(taskRepository.findTaskByTitle(taskDto.title())).thenReturn(Optional.empty());

        Task taskToSave = TaskMapper.toEntity(taskDto);
        when(taskRepository.save(taskToSave)).thenReturn(taskToSave);

        TaskDto savedTask = taskService.saveTask(taskDto);

        assertEquals(taskDto, savedTask);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTaskById_Success() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.of(task));

        taskService.deleteTaskById(taskDto.id());

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTaskById_TaskNotFound() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(taskDto.id()));
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.of(task));

        TaskDto taskToUpdateDto = TaskDto.builder()
                .title("Updated task")
                .description("updated description")
                .status(TaskStatus.CANCELLED)
                .build();

        TaskDto updatedTaskDto = taskService.updateTask(taskDto.id(), taskToUpdateDto);

        assertEquals(taskDto.id(), updatedTaskDto.id());
        assertEquals(taskToUpdateDto.title(), updatedTaskDto.title());
        assertEquals(taskToUpdateDto.description(), updatedTaskDto.description());
        assertEquals(taskToUpdateDto.status(), updatedTaskDto.status());
    }

    @Test
    void updateTask_SuccessWithNullableFields() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.of(task));

        TaskDto taskToUpdateDto = TaskDto.builder()
                .title(null)
                .description(null)
                .status(null)
                .build();

        TaskDto updatedTaskDto = taskService.updateTask(taskDto.id(), taskToUpdateDto);

        assertEquals(taskDto.id(), updatedTaskDto.id());
        assertEquals(taskDto.title(), updatedTaskDto.title());
        assertEquals(taskDto.description(), updatedTaskDto.description());
        assertEquals(taskDto.status(), updatedTaskDto.status());
    }

    @Test
    void updateTask_SuccessWithSameFields() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.of(task));

        TaskDto taskToUpdateDto = TaskDto.builder()
                .title(taskDto.title())
                .description(taskDto.description())
                .status(taskDto.status())
                .build();

        TaskDto updatedTaskDto = taskService.updateTask(taskDto.id(), taskToUpdateDto);

        assertEquals(taskDto.id(), updatedTaskDto.id());
        assertEquals(taskDto.title(), updatedTaskDto.title());
        assertEquals(taskDto.description(), updatedTaskDto.description());
        assertEquals(taskDto.status(), updatedTaskDto.status());
    }

    @Test
    void updateTask_TaskNotFound() {
        when(taskRepository.findById(taskDto.id())).thenReturn(Optional.empty());

        TaskDto taskToUpdateDto = TaskDto.builder()
                .title("Updated task")
                .description("updated description")
                .status(TaskStatus.CANCELLED)
                .build();

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskDto.id(), taskToUpdateDto));
    }
}