package ru.t1.demo.dto;

import lombok.Builder;
import ru.t1.demo.entity.TaskStatus;

import java.util.UUID;

@Builder
public record TaskDto(UUID id, String title, String description, TaskStatus status) {
}
