package com.pm.project_manager.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pm.project_manager.model.TaskStatus;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate deadline;
    private Long projectId;
    private Long assigneeId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}