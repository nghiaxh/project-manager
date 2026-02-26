package com.pm.project_manager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String message;
    private Long userId;
    private boolean isRead;
    private LocalDateTime createdAt;
}