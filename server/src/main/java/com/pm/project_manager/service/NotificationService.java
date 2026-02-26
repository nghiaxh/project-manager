package com.pm.project_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void sendNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setUser(user);
        notif.setRead(false);
        notificationRepository.save(notif);
    }

    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notif.setRead(true);
        notificationRepository.save(notif);
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