package com.pm.project_manager.intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.project_manager.dto.ProjectDto;
import com.pm.project_manager.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProjectControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProjectService projectService;

    @BeforeEach
    void setup() {
        User.withUsername("testuser")
                .password("123456")
                .roles("MEMBER")
                .build();
    }


    // GET PROJECT LIST
    @Test
    @WithMockUser(username = "testuser")
    void getUserProjects_shouldReturnList() throws Exception {
        ProjectDto project = new ProjectDto();
        project.setId(1L);
        project.setName("Test Project");

        when(projectService.getProjectsByUser("testuser"))
                .thenReturn(List.of(project));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    // CREATE PROJECT
    @Test
    @WithMockUser(username = "testuser")
    void createProject_shouldReturnProject() throws Exception {
        ProjectDto request = new ProjectDto();
        request.setName("New Project");

        ProjectDto response = new ProjectDto();
        response.setId(1L);
        response.setName("New Project");

        when(projectService.createProject(any(), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Project"));
    }


    // GET PROJECT BY ID
    @Test
    @WithMockUser(username = "testuser", roles = {"MEMBER"})
    void getProject_shouldReturnProject() throws Exception {
        ProjectDto project = new ProjectDto();
        project.setId(1L);
        project.setName("Detail Project");

        when(projectService.getProject(1L))
                .thenReturn(project);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Detail Project"));
    }

    // UPDATE PROJECT
    @Test
    @WithMockUser(username = "testuser")
    void updateProject_shouldReturnUpdatedProject() throws Exception {
        ProjectDto request = new ProjectDto();
        request.setName("Updated Project");

        ProjectDto response = new ProjectDto();
        response.setId(1L);
        response.setName("Updated Project");

        when(projectService.updateProject(eq(1L), any(), anyString()))
                .thenReturn(response);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));
    }

    // DELETE PROJECT
    @Test
    @WithMockUser(username = "testuser")
    void deleteProject_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk());
    }

    // ADD MEMBER
    @Test
    void addMember_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/projects/1/members")
                        .param("username", "user2"))
                .andExpect(status().isOk());
    }

    // REMOVE MEMBER
    @Test
    void removeMember_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/projects/1/members/2"))
                .andExpect(status().isOk());
    }

    // GET MEMBERS
    @Test
    void getMembers_shouldReturnList() throws Exception {
        when(projectService.getMembers(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/projects/1/members"))
                .andExpect(status().isOk());
    }
}