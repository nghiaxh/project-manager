package com.pm.project_manager.controller;

import com.pm.project_manager.dto.RegisterRequest;
import com.pm.project_manager.dto.UpdateUserRequest;
import com.pm.project_manager.dto.UserDto;
import com.pm.project_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody RegisterRequest request) {
        return userService.adminCreateUser(request);
    }

    @PutMapping("/users/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return userService.adminUpdateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.adminDeleteUser(id);
    }
}