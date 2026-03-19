package com.pm.project_manager.controller;

import com.pm.project_manager.dto.UpdateUserRequest;
import com.pm.project_manager.dto.UserDto;
import com.pm.project_manager.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return userService.getUserDtoByUsername(username);
    }

    @PutMapping("/me")
    public UserDto updateCurrentUser(@AuthenticationPrincipal UserDetails currentUser,
            @RequestBody UpdateUserRequest request) {
        return userService.updateCurrentUser(currentUser.getUsername(), request);
    }
}