package com.pm.project_manager.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private Double progress;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}