package com.pm.project_manager.controller;

import com.pm.project_manager.config.JwtUtils;
import com.pm.project_manager.dto.AuthResponse;
import com.pm.project_manager.dto.LoginRequest;
import com.pm.project_manager.dto.RegisterRequest;
import com.pm.project_manager.dto.UserDto;
import com.pm.project_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        UserDto user = userService.register(request);
        return new AuthResponse(user.getId(), user.getUsername(), user.getName(), user.getEmail(), null, user.getRole());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        UserDto user = userService.login(request.getUsername(), request.getPassword());
        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), user.getName(), user.getEmail(), token, user.getRole());
    }
}