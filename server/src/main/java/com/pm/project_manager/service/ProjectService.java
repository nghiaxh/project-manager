package com.pm.project_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pm.project_manager.dto.ProjectDto;
import com.pm.project_manager.dto.ProjectMemberDto;
import com.pm.project_manager.model.*;
import com.pm.project_manager.repository.ProjectMemberRepository;
import com.pm.project_manager.repository.ProjectRepository;
import com.pm.project_manager.repository.UserRepository;
import com.pm.project_manager.repository.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserService userService;
    private final TaskRepository taskRepository;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto, String creatorUsername) {
        User creator = userService.getUserByUsername(creatorUsername);

        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setDeadline(projectDto.getDeadline());
        project.setCreatedBy(creator);

        Project saved = projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setProject(saved);
        member.setUser(creator);
        member.setRole(ProjectRole.MANAGER);
        projectMemberRepository.save(member);

        return mapToDto(saved);
    }

    public List<ProjectDto> getProjectsByUser(String username) {
        User user = userService.getUserByUsername(username);
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(user.getId());
        return memberships.stream()
                .map(pm -> mapToDto(pm.getProject()))
                .collect(Collectors.toList());
    }

    public ProjectDto getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToDto(project);
    }

    private boolean isProjectAdminOrManager(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (project.getCreatedBy().getUsername().equals(username)) {
            return true;
        }
        User user = userService.getUserByUsername(username);
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .orElse(null);
        return member != null && (member.getRole() == ProjectRole.ADMIN || member.getRole() == ProjectRole.MANAGER);
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto dto, String currentUsername) {
        if (!isProjectAdminOrManager(id, currentUsername)) {
            throw new RuntimeException("You don't have permission to update this project!");
        }
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDeadline(dto.getDeadline());
        return mapToDto(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id, String currentUsername) {
        if (!isProjectAdminOrManager(id, currentUsername)) {
            throw new RuntimeException("You don't have permission to delete this project!");
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public void addMember(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new RuntimeException("User already member of this project");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(ProjectRole.MEMBER);
        projectMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this project"));

        if (member.getUser().getId().equals(project.getCreatedBy().getId())) {
            throw new RuntimeException("Cannot remove the project creator");
        }

        projectMemberRepository.delete(member);
    }

    public List<ProjectMemberDto> getMembers(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::mapMemberToDto)
                .collect(Collectors.toList());
    }

    private double calculateProgress(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        long total = tasks.size();
        if (total == 0)
            return 0.0;
        long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        return (double) done / total * 100;
    }

    private ProjectDto mapToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedBy(project.getCreatedBy().getUsername());
        dto.setDeadline(project.getDeadline());
        dto.setProgress(calculateProgress(project.getId()));
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }

    private ProjectMemberDto mapMemberToDto(ProjectMember pm) {
        ProjectMemberDto dto = new ProjectMemberDto();
        dto.setId(pm.getId());
        dto.setProjectId(pm.getProject().getId());
        dto.setUserId(pm.getUser().getId());
        dto.setName(pm.getUser().getName());
        dto.setUsername(pm.getUser().getUsername());
        dto.setRole(pm.getRole());
        return dto;
    }
}