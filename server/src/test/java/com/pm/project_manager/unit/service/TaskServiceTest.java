package com.pm.project_manager.unit.service;

import com.pm.project_manager.dto.CommentDto;
import com.pm.project_manager.dto.TaskDto;
import com.pm.project_manager.model.*;
import com.pm.project_manager.repository.*;
import com.pm.project_manager.service.NotificationService;
import com.pm.project_manager.service.TaskService;
import com.pm.project_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_success_withAssignee() {
        Long projectId = 1L;
        String username = "hung";

        TaskDto dto = new TaskDto();
        dto.setTitle("Task");
        dto.setStatus(TaskStatus.TODO);
        dto.setAssigneeId(2L);

        Project project = new Project();
        project.setId(projectId);
        User creator = new User();
        creator.setId(1L);
        User assignee = new User();
        assignee.setId(2L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userService.getUserByUsername(username)).thenReturn(creator);
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TaskDto result = taskService.createTask(projectId, dto, username);

        assertNotNull(result);
        verify(notificationService).sendNotification(eq(2L), contains("Task"), anyString());
    }

    @Test
    void createTask_fail_projectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taskService.createTask(1L, new TaskDto(), "hung"));
    }

    @Test
    void getTask_success() {
        Task task = new Task();
        task.setId(1L);
        task.setProject(new Project());
        task.getProject().setId(10L);
        task.setCreatedBy(new User());
        task.getCreatedBy().setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto dto = taskService.getTask(1L);

        assertEquals(1L, dto.getId());
    }

    @Test
    void getTask_fail_notFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taskService.getTask(1L));
    }

    @Test
    void getTasksByProject_success() {
        Task task = new Task();
        task.setId(1L);
        task.setProject(new Project());
        task.getProject().setId(10L);
        task.setCreatedBy(new User());
        task.getCreatedBy().setId(1L);

        when(taskRepository.findByProjectId(10L))
                .thenReturn(List.of(task));

        List<TaskDto> result = taskService.getTasksByProject(10L);

        assertEquals(1, result.size());
    }

    @Test
    void updateTask_success_noStatusChange() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.TODO);
        Project project = new Project();
        project.setId(10L);
        task.setProject(project);
        User creator = new User();
        creator.setId(1L);
        task.setCreatedBy(creator);
        task.setTitle("Task");

        TaskDto dto = new TaskDto();
        dto.setTitle("Task");
        dto.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        taskService.updateTask(1L, dto);

        verify(notificationService, never()).sendNotification(anyLong(), anyString(), anyString());
    }

    @Test
    void updateTask_sendNotification_whenStatusChanged() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task");
        task.setStatus(TaskStatus.TODO);
        Project project = new Project();
        project.setId(10L);
        task.setProject(project);
        User creator = new User();
        creator.setId(1L);
        task.setCreatedBy(creator);
        task.setAssignee(null);

        TaskDto dto = new TaskDto();
        dto.setTitle("Task");
        dto.setStatus(TaskStatus.DONE);

        User user = new User();
        user.setId(2L);
        ProjectMember member = new ProjectMember();
        member.setUser(user);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(projectMemberRepository.findByProjectId(10L))
                .thenReturn(List.of(member));

        taskService.updateTask(1L, dto);

        verify(notificationService).sendNotification(eq(2L), contains("Hoàn thành"), anyString());
    }

    @Test
    void updateTask_fail_notFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taskService.updateTask(1L, new TaskDto()));
    }

    @Test
    void deleteTask_success() {
        taskService.deleteTask(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void addComment_success_notifyOthers() {
        Task task = new Task();
        task.setId(1L);
        Project project = new Project();
        project.setId(10L);
        task.setProject(project);
        User user = new User();
        user.setId(1L);
        user.setUsername("hung");
        User other = new User();
        other.setId(2L);
        ProjectMember member = new ProjectMember();
        member.setUser(other);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserByUsername("hung")).thenReturn(user);
        when(commentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(projectMemberRepository.findByProjectId(10L))
                .thenReturn(List.of(member));

        taskService.addComment(1L, "hung", "Hello");

        verify(notificationService).sendNotification(eq(2L), contains("Hello"), anyString());
    }

    @Test
    void addComment_fail_taskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> taskService.addComment(1L, "hung", "Hello"));
    }

    @Test
    void getComments_success() {
        Comment c = new Comment();
        c.setId(1L);
        c.setTask(new Task());
        c.getTask().setId(1L);
        User u = new User();
        u.setId(1L);
        u.setUsername("hung");
        c.setUser(u);

        when(commentRepository.findByTaskId(1L))
                .thenReturn(List.of(c));

        List<CommentDto> result = taskService.getComments(1L);
        assertEquals(1, result.size());
    }
}