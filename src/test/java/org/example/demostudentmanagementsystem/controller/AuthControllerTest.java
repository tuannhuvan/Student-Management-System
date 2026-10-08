package org.example.demostudentmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.demostudentmanagementsystem.config.RateLimitInterceptor;
import org.example.demostudentmanagementsystem.dto.RegisterRequest;
import org.example.demostudentmanagementsystem.dto.RegisterResponse;
import org.example.demostudentmanagementsystem.entity.Role;
import org.example.demostudentmanagementsystem.exception.DuplicateResourceException;
import org.example.demostudentmanagementsystem.exception.GlobalExceptionHandler;
import org.example.demostudentmanagementsystem.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, RateLimitInterceptor.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest(
                "johndoe",
                "john.doe@university.edu",
                "SecurePass123!",
                "John",
                "Doe"
        );
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 201 Created with Location header when valid")
    void register_ValidPayload_Returns201Created() throws Exception {
        RegisterResponse response = new RegisterResponse(
                101L,
                "johndoe",
                "john.doe@university.edu",
                "John",
                "Doe",
                Role.ROLE_STUDENT,
                Instant.now()
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/101"))
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@university.edu"))
                .andExpect(jsonPath("$.role").value("ROLE_STUDENT"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 400 Bad Request when validation fails")
    void register_InvalidEmail_Returns400BadRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest(
                "john",
                "invalid-email-format",
                "123",
                "John",
                "Doe"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("AUTH_VALIDATION_FAILED"))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 409 Conflict when email is duplicate")
    void register_DuplicateEmail_Returns409Conflict() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Email is already in use", "AUTH_EMAIL_ALREADY_EXISTS"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("AUTH_EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.detail").value("Email is already in use"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 429 Too Many Requests when rate limit exceeded")
    void register_RateLimitExceeded_Returns429TooManyRequests() throws Exception {
        RegisterResponse response = new RegisterResponse(
                101L,
                "johndoe",
                "john.doe@university.edu",
                "John",
                "Doe",
                Role.ROLE_STUDENT,
                Instant.now()
        );
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // Send 5 permitted requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/register")
                            .header("X-Forwarded-For", "192.168.1.100")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated());
        }

        // 6th request from same IP should fail with 429
        mockMvc.perform(post("/api/v1/auth/register")
                        .header("X-Forwarded-For", "192.168.1.100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(jsonPath("$.errorCode").value("AUTH_RATE_LIMIT_EXCEEDED"));
    }
}
