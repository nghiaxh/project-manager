package com.pm.project_manager.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String currentPassword;
    private String newPassword;
}