package ru.t1.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.t1.demo.TestContainersConfig;
import ru.t1.demo.dto.TaskDto;
import ru.t1.demo.entity.Task;
import ru.t1.demo.entity.TaskStatus;
import ru.t1.demo.exception.TaskIsPresentException;
import ru.t1.demo.exception.TaskNotFoundException;
import ru.t1.demo.repository.TaskRepository;
import ru.t1.demo.util.TaskMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest {

    @Autowired
    PostgreSQLContainer postgreSQLContainer;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        postgreSQLContainer.start();
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        taskRepository.deleteAll();
    }

    @Test
    void testConnection() {
        assertTrue(postgreSQLContainer.isRunning());
        Assertions.assertEquals("testDatabase", postgreSQLContainer.getDatabaseName());
        Assertions.assertEquals("testUser", postgreSQLContainer.getUsername());
        Assertions.assertEquals("testPassword", postgreSQLContainer.getPassword());
    }

    @Test
    void testSaveTask() {
        Task task = Task.builder()
                .title("test Title")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        Task savedTask = taskRepository.save(task);
        Assertions.assertNotNull(savedTask.getId());
    }


    @Test
    void getAllTasks_EmptyList() throws Exception {

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTaskById_Success() throws Exception {
        Task task = Task.builder()
                .title("test Title")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        Task savedTask = taskRepository.save(task);
        TaskDto savedTaskDto = TaskMapper.toDto(savedTask);

        mockMvc.perform(get("/api/v1/tasks/{id}", savedTaskDto.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(savedTaskDto.title()));
    }

    @Test
    void saveTask_TaskIsPresent() throws Exception {
        Task existingTask = Task.builder()
                .title("test Title")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        taskRepository.save(existingTask);

        Task newTask = Task.builder()
                .title("test Title")
                .description(null)
                .status(TaskStatus.IN_PROGRESS)
                .build();


        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof TaskIsPresentException))
                .andExpect(result -> assertEquals("Task is already present", result.getResolvedException().getMessage())
                );

    }

    @Test
    void deleteTask_TaskNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof TaskNotFoundException))
                .andExpect(result -> assertEquals("Task with id" + taskId + " not found", result.getResolvedException().getMessage())
                );

    }

    @Test
    void updateTask_Success() throws Exception {
        Task existingTask = Task.builder()
                .title("test Title")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        Task savedTask = taskRepository.save(existingTask);
        TaskDto savedTaskDto = TaskMapper.toDto(savedTask);


        Task newTask = Task.builder()
                .title("new title")
                .description("some description")
                .status(TaskStatus.COMPLETED)
                .build();

        TaskDto newTaskDto = TaskMapper.toDto(newTask);

        mockMvc.perform(put("/api/v1/tasks/{id}", savedTaskDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(newTaskDto.description()));


    }
}
