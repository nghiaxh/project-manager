package com.pm.project_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pm.project_manager.dto.NotificationDto;
import com.pm.project_manager.model.Notification;
import com.pm.project_manager.model.User;
import com.pm.project_manager.repository.NotificationRepository;
import com.pm.project_manager.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public void sendNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setUser(user);
        notif.setRead(false);
        notificationRepository.save(notif);
    }

    public List<NotificationDto> getUserNotifications(String username) {
        User user = userService.getUserByUsername(username);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notif.setRead(true);
        notificationRepository.save(notif);
    }

    @Transactional
    public void markAllAsRead(String username) {
        User user = userService.getUserByUsername(username);
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalse(user.getId());
        unreadNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(Long id, String username) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notif.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You don't have permission to delete this notification");
        }
        notificationRepository.delete(notif);
    }

    @Transactional
    public void deleteReadNotifications(String username) {
        User user = userService.getUserByUsername(username);
        List<Notification> readNotifications = notificationRepository.findByUserIdAndReadTrue(user.getId());
        notificationRepository.deleteAll(readNotifications);
    }

    private NotificationDto mapToDto(Notification notif) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notif.getId());
        dto.setMessage(notif.getMessage());
        dto.setUserId(notif.getUser().getId());
        dto.setRead(notif.getRead());
        dto.setCreatedAt(notif.getCreatedAt());
        return dto;
    }
}