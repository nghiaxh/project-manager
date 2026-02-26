package com.pm.project_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.pm.project_manager.dto.RegisterRequest;
import com.pm.project_manager.dto.UserDto;
import com.pm.project_manager.model.User;
import com.pm.project_manager.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // Encrypt password later
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    public UserDto login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username/password"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username/password");
        }
        return mapToDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}