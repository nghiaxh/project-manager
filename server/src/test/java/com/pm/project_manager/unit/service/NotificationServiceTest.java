package com.pm.project_manager.unit.service;

import com.pm.project_manager.dto.NotificationDto;
import com.pm.project_manager.model.Notification;
import com.pm.project_manager.model.User;
import com.pm.project_manager.repository.NotificationRepository;
import com.pm.project_manager.repository.UserRepository;
import com.pm.project_manager.service.NotificationService;
import com.pm.project_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    void sendNotification_success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.sendNotification(1L, "Hello");

        verify(notificationRepository).save(argThat(n ->
                n.getUser().getId().equals(1L)
                        && n.getMessage().equals("Hello")
                        && !n.getRead()
        ));
    }

    @Test
    void sendNotification_fail_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificationService.sendNotification(1L, "Hello"));
    }


    @Test
    void getUserNotifications_success() {
        User user = new User();
        user.setId(1L);

        Notification notif = new Notification();
        notif.setId(10L);
        notif.setMessage("Hi");
        notif.setUser(user);
        notif.setRead(false);

        when(userService.getUserByUsername("hung")).thenReturn(user);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notif));

        List<NotificationDto> result = notificationService.getUserNotifications("hung");

        assertEquals(1, result.size());
        assertEquals("Hi", result.get(0).getMessage());
    }

    @Test
    void markAsRead_success() {
        Notification notif = new Notification();
        notif.setId(1L);
        notif.setRead(false);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));

        notificationService.markAsRead(1L);

        assertTrue(notif.getRead());
        verify(notificationRepository).save(notif);
    }

    @Test
    void markAsRead_fail_notFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(1L));
    }


    @Test
    void markAllAsRead_success() {
        User user = new User();
        user.setId(1L);

        Notification n1 = new Notification();
        n1.setRead(false);

        Notification n2 = new Notification();
        n2.setRead(false);

        when(userService.getUserByUsername("hung")).thenReturn(user);
        when(notificationRepository.findByUserIdAndReadFalse(1L))
                .thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead("hung");

        assertTrue(n1.getRead());
        assertTrue(n2.getRead());

        verify(notificationRepository).saveAll(any());
    }


    @Test
    void deleteNotification_success() {
        User user = new User();
        user.setUsername("hung");

        Notification notif = new Notification();
        notif.setUser(user);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));

        notificationService.deleteNotification(1L, "hung");

        verify(notificationRepository).delete(notif);
    }

    @Test
    void deleteNotification_fail_notOwner() {
        User user = new User();
        user.setUsername("other");

        Notification notif = new Notification();
        notif.setUser(user);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));

        assertThrows(RuntimeException.class,
                () -> notificationService.deleteNotification(1L, "hung"));
    }

    @Test
    void deleteNotification_fail_notFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificationService.deleteNotification(1L, "hung"));
    }


    @Test
    void deleteReadNotifications_success() {
        User user = new User();
        user.setId(1L);

        Notification n1 = new Notification();
        Notification n2 = new Notification();

        when(userService.getUserByUsername("hung")).thenReturn(user);
        when(notificationRepository.findByUserIdAndReadTrue(1L))
                .thenReturn(List.of(n1, n2));

        notificationService.deleteReadNotifications("hung");

        verify(notificationRepository).deleteAll(List.of(n1, n2));
    }
}