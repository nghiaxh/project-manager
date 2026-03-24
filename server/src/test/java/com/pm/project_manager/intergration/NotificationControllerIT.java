package com.pm.project_manager.intergration;

import com.pm.project_manager.dto.NotificationDto;
import com.pm.project_manager.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NotificationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    //GET notifications
    @Test
    @WithMockUser(username = "hung")
    void getUserNotifications_shouldReturnList() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.setId(1L);
        dto.setMessage("Test notification");

        when(notificationService.getUserNotifications("hung"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Test notification"));

        verify(notificationService).getUserNotifications("hung");
    }

    //Mark as read
    @Test
    @WithMockUser
    void markAsRead_shouldCallService() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1L);
    }

    //Mark all as read
    @Test
    @WithMockUser(username = "hung")
    void markAllAsRead_shouldUseUsername() throws Exception {
        mockMvc.perform(put("/api/notifications/read-all"))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead("hung");
    }

    //DELETE notification
    @Test
    @WithMockUser(username = "hung")
    void deleteNotification_shouldCallServiceWithUsername() throws Exception {
        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isOk());

        verify(notificationService).deleteNotification(1L, "hung");
    }

    //DELETE read notification
    @Test
    @WithMockUser(username = "hung")
    void deleteReadNotifications_shouldCallService() throws Exception {
        mockMvc.perform(delete("/api/notifications/read"))
                .andExpect(status().isOk());

        verify(notificationService).deleteReadNotifications("hung");
    }
}