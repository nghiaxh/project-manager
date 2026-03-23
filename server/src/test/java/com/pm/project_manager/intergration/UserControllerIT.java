package com.pm.project_manager.intergration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.project_manager.dto.UpdateUserRequest;
import com.pm.project_manager.model.User;
import com.pm.project_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setName("Nguyen Van Hung");
        user.setUsername("hung");
        user.setEmail("hung@gmail.com");
        user.setPassword(passwordEncoder.encode("123"));
        savedUser = userRepository.save(user);
    }


    // GET /api/users/{id}
    @Test
    @WithMockUser(username = "hung")
    void getUserById_success() throws Exception {
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hung@gmail.com"))
                .andExpect(jsonPath("$.username").value("hung"));
    }
    // GET /api/users/me
    @Test
    @WithMockUser(username = "hung")
    void getCurrentUser_success() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("hung"))
                .andExpect(jsonPath("$.email").value("hung@gmail.com"));
    }

    // PUT /api/users/me
    @Test
    @WithMockUser(username = "hung")
    void updateCurrentUser_success() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@gmail.com");

        mockMvc.perform(put("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@gmail.com"));

        // verify DB
        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("new@gmail.com", updated.getEmail());
    }
}