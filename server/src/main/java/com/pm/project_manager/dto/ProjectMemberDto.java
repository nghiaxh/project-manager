package com.pm.project_manager.dto;

import com.pm.project_manager.model.ProjectRole;
import lombok.Data;

@Data
public class ProjectMemberDto {
    private Long id;
    private Long projectId;
    private Long userId;
    private String username;
    private String name;
    private ProjectRole role;
}