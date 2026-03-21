package com.pm.project_manager.controller;

import com.pm.project_manager.dto.CommentDto;
import com.pm.project_manager.dto.TaskDto;
import com.pm.project_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskDto> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public TaskDto createTask(@PathVariable Long projectId,
            @RequestBody TaskDto dto,
            @AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return taskService.createTask(projectId, dto, username);
    }

    @GetMapping("/tasks/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/tasks/{taskId}/comments")
    public CommentDto addComment(@PathVariable Long taskId,
            @RequestBody String content,
            @AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return taskService.addComment(taskId, username, content);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentDto> getComments(@PathVariable Long taskId) {
        return taskService.getComments(taskId);
    }
}