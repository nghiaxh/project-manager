package com.pm.project_manager.controller;

import com.pm.project_manager.dto.NotificationDto;
import com.pm.project_manager.service.NotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getUserNotifications(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return notificationService.getUserNotifications(username);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    public void markAllAsRead(@AuthenticationPrincipal UserDetails currentUser) {
        notificationService.markAllAsRead(currentUser.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        notificationService.deleteNotification(id, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/read")
    public ResponseEntity<?> deleteReadNotifications(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        notificationService.deleteReadNotifications(username);
        return ResponseEntity.ok().build();
    }
}