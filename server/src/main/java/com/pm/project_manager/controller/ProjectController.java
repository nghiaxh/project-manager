package com.pm.project_manager.controller;

import com.pm.project_manager.dto.ProjectDto;
import com.pm.project_manager.dto.ProjectMemberDto;
import com.pm.project_manager.model.ProjectRole;
import com.pm.project_manager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectDto> getUserProjects(@RequestParam Long userId) {
        return projectService.getProjectsByUser(userId);
    }

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectDto dto, @RequestParam Long createdBy) {
        return projectService.createProject(dto, createdBy);
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectDto dto) {
        return projectService.updateProject(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{projectId}/members")
    public void addMember(@PathVariable Long projectId, @RequestParam Long userId, @RequestParam ProjectRole role) {
        projectService.addMember(projectId, userId, role);
    }

    @GetMapping("/{projectId}/members")
    public List<ProjectMemberDto> getMembers(@PathVariable Long projectId) {
        return projectService.getMembers(projectId);
    }
}