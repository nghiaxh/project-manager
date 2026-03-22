package com.pm.project_manager.unit.service;

import com.pm.project_manager.dto.RegisterRequest;
import com.pm.project_manager.dto.UpdateUserRequest;
import com.pm.project_manager.repository.UserRepository;
import com.pm.project_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.pm.project_manager.model.User;
import com.pm.project_manager.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void register_shouldSuccess() {
        // GIVEN
        RegisterRequest request = buildRegisterRequest();

        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encoded123");

        User savedUser = buildUser(1L);
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // WHEN
        UserDto result = userService.register(request);

        // THEN
        assertAll(
                () -> assertEquals("hung", result.getUsername()),
                () -> assertEquals("Hung", result.getName()),
                () -> assertEquals("hung@gmail.com", result.getEmail())
        );

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenUsernameExists() {
        // GIVEN
        RegisterRequest request = buildRegisterRequest();

        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.of(new User()));

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.register(request));

        assertEquals("Username already exists", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldSuccess() {
        // GIVEN
        User user = buildUser(1L);
        user.setPassword("encoded");

        when(userRepository.findByUsername("hung"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encoded"))
                .thenReturn(true);

        // WHEN
        UserDto result = userService.login("hung", "123");

        // THEN
        assertEquals("hung", result.getUsername());
    }

    @Test
    void login_shouldFail_whenUserNotFound() {
        when(userRepository.findByUsername("hung"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.login("hung", "123"));
    }

    @Test
    void login_shouldFail_whenWrongPassword() {
        User user = buildUser(1L);
        user.setPassword("encoded");

        when(userRepository.findByUsername("hung"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encoded"))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.login("hung", "123"));
    }


    @Test
    void getUserById_shouldSuccess() {
        User user = buildUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals("hung", result.getUsername());
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUserById(1L));
    }

    // ================= UPDATE =================

    @Test
    void updateCurrentUser_shouldUpdateNameAndEmail() {
        // GIVEN
        User user = buildUser(1L);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("New Name");
        request.setEmail("new@gmail.com");

        when(userRepository.findByUsername("hung"))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@gmail.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        // WHEN
        UserDto result = userService.updateCurrentUser("hung", request);

        // THEN
        assertAll(
                () -> assertEquals("New Name", result.getName()),
                () -> assertEquals("new@gmail.com", result.getEmail())
        );

        verify(userRepository).save(argThat(u ->
                u.getName().equals("New Name") &&
                        u.getEmail().equals("new@gmail.com")
        ));
    }

    @Test
    void updateCurrentUser_shouldFail_whenWrongCurrentPassword() {
        User user = buildUser(1L);
        user.setPassword("encoded");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("new123");

        when(userRepository.findByUsername("hung"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.updateCurrentUser("hung", request));
    }

    @Test
    void updateCurrentUser_shouldFail_whenEmailExists() {
        User user = buildUser(1L);

        User existing = buildUser(2L);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("test@gmail.com");

        when(userRepository.findByUsername("hung"))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class,
                () -> userService.updateCurrentUser("hung", request));
    }


    private RegisterRequest buildRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("hung");
        request.setPassword("123");
        request.setName("Hung");
        request.setEmail("hung@gmail.com");
        return request;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("hung");
        user.setName("Hung");
        user.setEmail("hung@gmail.com");
        return user;
    }
}