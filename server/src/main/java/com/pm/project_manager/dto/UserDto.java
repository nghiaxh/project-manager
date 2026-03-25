package com.pm.project_manager.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;
}