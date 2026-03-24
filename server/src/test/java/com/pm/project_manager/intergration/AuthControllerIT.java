package com.pm.project_manager.intergration;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.project_manager.config.JwtUtils;
import com.pm.project_manager.dto.LoginRequest;
import com.pm.project_manager.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // tắt security
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        // fake token
        when(jwtUtils.generateToken(any()))
                .thenReturn("fake-token");
    }


    // TEST REGISTER
    @Test
    void register_shouldReturnUserInfo_withoutToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setEmail("test@gmail.com");
        request.setName("Test User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    // TEST LOGIN SUCCESS
    @Test
    void login_shouldReturnToken_whenValidCredentials() throws Exception {

        // register trước
        RegisterRequest register = new RegisterRequest();
        register.setUsername("loginuser");
        register.setPassword("123456");
        register.setEmail("login@gmail.com");
        register.setName("Login User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // login
        LoginRequest login = new LoginRequest();
        login.setUsername("loginuser");
        login.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("loginuser"))
                .andExpect(jsonPath("$.token").value("fake-token"));
    }


    // TEST LOGIN FAIL
    @Test
    void login_shouldFail_whenWrongPassword() throws Exception {

        // tạo user
        RegisterRequest register = new RegisterRequest();
        register.setUsername("failuser");
        register.setPassword("123456");
        register.setEmail("fail@gmail.com");
        register.setName("Fail User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // login sai password
        LoginRequest login = new LoginRequest();
        login.setUsername("failuser");
        login.setPassword("wrong");

        // throw ServletException
        Exception thrown = assertThrows(
                jakarta.servlet.ServletException.class,
                () -> mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
        );

        Throwable cause = thrown.getCause();
        assertInstanceOf(RuntimeException.class, cause);
        assertEquals("Invalid username/password", cause.getMessage());
    }
}