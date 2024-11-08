package ru.t1.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.demo.dto.TaskDto;
import ru.t1.demo.entity.Task;

@Component
public class TaskMapper {

    private TaskMapper() {
    }

    public static Task toEntity(TaskDto dto) {
        return Task.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(dto.status())
                .build();
    }

    public static TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }
}
