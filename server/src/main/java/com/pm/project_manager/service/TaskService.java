package com.pm.project_manager.service;

import com.pm.project_manager.dto.CommentDto;
import com.pm.project_manager.dto.TaskDto;
import com.pm.project_manager.model.*;
import com.pm.project_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TaskDto createTask(Long projectId, TaskDto dto, String creatorUsername) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User creator = userService.getUserByUsername(creatorUsername);

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setDeadline(dto.getDeadline());
        task.setProject(project);
        task.setCreatedBy(creator);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null) {
            notificationService.sendNotification(
                    saved.getAssignee().getId(),
                    "Bạn được giao công việc: " + saved.getTitle());
        }

        return mapToDto(saved);
    }

    public List<TaskDto> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDto(task);
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskStatus oldStatus = task.getStatus();

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setDeadline(dto.getDeadline());

        if (dto.getAssigneeId() != null
                && !dto.getAssigneeId().equals(task.getAssignee() != null ? task.getAssignee().getId() : null)) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task updated = taskRepository.save(task);

        if (oldStatus != updated.getStatus()) {
            String statusText = getStatusText(updated.getStatus());
            String message = String.format("Công việc '%s' đã chuyển sang trạng thái: %s",
                    updated.getTitle(), statusText);

            List<ProjectMember> members = projectMemberRepository.findByProjectId(updated.getProject().getId());
            for (ProjectMember member : members) {
                notificationService.sendNotification(member.getUser().getId(), message);
            }
        }

        return mapToDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional
    public CommentDto addComment(Long taskId, String username, String content) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userService.getUserByUsername(username);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTask(task);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);

        String commentMsg = String.format("%s đã bình luận: %s", user.getUsername(),
                content);
        List<ProjectMember> members = projectMemberRepository.findByProjectId(task.getProject().getId());
        members.forEach(m -> {
            if (!m.getUser().getId().equals(user.getId())) {
                notificationService.sendNotification(m.getUser().getId(), commentMsg);
            }
        });

        CommentDto dto = mapCommentToDto(saved);
        messagingTemplate.convertAndSend("/topic/tasks/" + taskId + "/comments", dto);

        return mapCommentToDto(saved);
    }

    public List<CommentDto> getComments(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    private String getStatusText(TaskStatus status) {
        switch (status) {
            case TODO:
                return "Cần làm";
            case IN_PROGRESS:
                return "Đang làm";
            case IN_REVIEW:
                return "Chờ duyệt";
            case DONE:
                return "Hoàn thành";
            default:
                return status.toString();
        }
    }

    private TaskDto mapToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setDeadline(task.getDeadline());
        dto.setProjectId(task.getProject().getId());
        dto.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);
        dto.setCreatedBy(task.getCreatedBy().getId());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    private CommentDto mapCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setTaskId(comment.getTask().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setName(comment.getUser().getName());
        dto.setUsername(comment.getUser().getUsername());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}