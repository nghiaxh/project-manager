package com.pm.project_manager.unit.service;

import com.pm.project_manager.dto.ProjectDto;
import com.pm.project_manager.model.Project;
import com.pm.project_manager.model.ProjectMember;
import com.pm.project_manager.model.User;
import com.pm.project_manager.repository.ProjectMemberRepository;
import com.pm.project_manager.repository.ProjectRepository;
import com.pm.project_manager.repository.UserRepository;
import com.pm.project_manager.service.ProjectService;
import com.pm.project_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    // ================= CREATE PROJECT =================

    @Test
    void createProject_shouldSuccess() {
        // GIVEN
        User creator = new User();
        creator.setId(1L);
        creator.setUsername("hung");

        ProjectDto dto = new ProjectDto();
        dto.setName("Project A");
        dto.setDescription("Desc");

        when(userService.getUserByUsername("hung")).thenReturn(creator);

        Project saved = new Project();
        saved.setId(1L);
        saved.setName("Project A");
        saved.setDescription("Desc");
        saved.setCreatedBy(creator);

        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        // WHEN
        ProjectDto result = projectService.createProject(dto, "hung");

        // THEN
        assertEquals("Project A", result.getName());

        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    // ================= GET PROJECT =================

    @Test
    void getProject_shouldSuccess() {
        User creator = new User();
        creator.setUsername("hung");

        Project project = new Project();
        project.setId(1L);
        project.setName("Test");
        project.setCreatedBy(creator);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        ProjectDto result = projectService.getProject(1L);

        assertEquals("Test", result.getName());
    }

    @Test
    void getProject_shouldThrow_whenNotFound() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> projectService.getProject(1L));
    }

    // ================= UPDATE PROJECT =================

    @Test
    void updateProject_shouldSuccess_whenUserIsOwner() {
        User creator = new User();
        creator.setId(1L);
        creator.setUsername("hung");

        Project project = new Project();
        project.setId(1L);
        project.setCreatedBy(creator);

        ProjectDto dto = new ProjectDto();
        dto.setName("Updated");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(projectRepository.save(any(Project.class)))
                .thenReturn(project);

        ProjectDto result = projectService.updateProject(1L, dto, "hung");

        assertEquals("Updated", result.getName());
    }

    @Test
    void updateProject_shouldFail_whenNoPermission() {
        // GIVEN
        User creator = new User();
        creator.setId(1L);
        creator.setUsername("owner");

        Project project = new Project();
        project.setId(1L);
        project.setCreatedBy(creator);

        User currentUser = new User();
        currentUser.setId(2L);
        currentUser.setUsername("user");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(userService.getUserByUsername("user"))
                .thenReturn(currentUser);

        when(projectMemberRepository.findByProjectIdAndUserId(1L, 2L))
                .thenReturn(Optional.empty()); // không phải member

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> projectService.updateProject(1L, new ProjectDto(), "user"));

        assertEquals("You don't have permission to update this project", ex.getMessage());
    }

    // ================= ADD MEMBER =================

    @Test
    void addMember_shouldSuccess() {
        Project project = new Project();
        project.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setUsername("user");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L))
                .thenReturn(false);

        projectService.addMember(1L, "user");

        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void addMember_shouldFail_whenAlreadyMember() {
        Project project = new Project();
        project.setId(1L);

        User user = new User();
        user.setId(2L);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L))
                .thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> projectService.addMember(1L, "user"));
    }

    // ================= REMOVE MEMBER =================

    @Test
    void removeMember_shouldSuccess() {
        User creator = new User();
        creator.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setCreatedBy(creator);

        User memberUser = new User();
        memberUser.setId(2L);

        ProjectMember member = new ProjectMember();
        member.setUser(memberUser);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 2L))
                .thenReturn(Optional.of(member));

        projectService.removeMember(1L, 2L);

        verify(projectMemberRepository).delete(member);
    }

    @Test
    void removeMember_shouldFail_whenRemoveCreator() {
        User creator = new User();
        creator.setId(1L);

        Project project = new Project();
        project.setCreatedBy(creator);

        ProjectMember member = new ProjectMember();
        member.setUser(creator);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(member));

        assertThrows(RuntimeException.class,
                () -> projectService.removeMember(1L, 1L));
    }
}
