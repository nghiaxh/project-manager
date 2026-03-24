package com.pm.project_manager.intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.project_manager.dto.CommentDto;
import com.pm.project_manager.dto.TaskDto;
import com.pm.project_manager.service.TaskService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //GET task theo project
    @Test
    @WithMockUser(username = "hung")
    void getTasksByProject_shouldReturnList() throws Exception {
        TaskDto task = new TaskDto();
        task.setId(1L);
        task.setTitle("Task 1");

        when(taskService.getTasksByProject(1L))
                .thenReturn(List.of(task));

        mockMvc.perform(get("/api/projects/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    //POST create task
    @Test
    @WithMockUser(username = "hung")
    void createTask_shouldReturnCreatedTask() throws Exception {
        TaskDto request = new TaskDto();
        request.setTitle("New Task");

        TaskDto response = new TaskDto();
        response.setId(1L);
        response.setTitle("New Task");

        when(taskService.createTask(eq(1L), any(), eq("hung")))
                .thenReturn(response);

        mockMvc.perform(post("/api/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    //GET task by id
    @Test
    void getTask_shouldReturnTask() throws Exception {
        TaskDto task = new TaskDto();
        task.setId(1L);
        task.setTitle("Task A");

        when(taskService.getTask(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task A"));
    }


    //PUT update task
    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        TaskDto request = new TaskDto();
        request.setTitle("Updated");

        TaskDto response = new TaskDto();
        response.setId(1L);
        response.setTitle("Updated");

        when(taskService.updateTask(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }
    //DELETE task
    @Test
    void deleteTask_shouldReturnOk() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    //POST comment (@AuthenticationPrincipal)
    @Test
    @WithMockUser(username = "hung")
    void addComment_shouldReturnComment() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setContent("hello");

        when(taskService.addComment(eq(1L), eq("hung"), any()))
                .thenReturn(comment);

        mockMvc.perform(post("/api/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"hello\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("hello"));
    }

    //GET comments
    @Test
    void getComments_shouldReturnList() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setContent("hello");

        when(taskService.getComments(1L))
                .thenReturn(List.of(comment));

        mockMvc.perform(get("/api/tasks/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("hello"));
    }
}