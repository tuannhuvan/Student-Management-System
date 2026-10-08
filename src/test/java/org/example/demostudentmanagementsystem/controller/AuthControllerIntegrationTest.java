package org.example.demostudentmanagementsystem.controller;

import org.example.demostudentmanagementsystem.dto.RegisterRequest;
import org.example.demostudentmanagementsystem.dto.RegisterResponse;
import org.example.demostudentmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Full End-to-End Registration Flow Test")
    void fullRegistrationFlow_Success() {
        RegisterRequest request = new RegisterRequest(
                "realuser",
                "real.user@university.edu",
                "SecurePassword123!",
                "Real",
                "User"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<RegisterResponse> response = restTemplate.postForEntity(
                "/api/v1/auth/register",
                entity,
                RegisterResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).startsWith("/api/v1/users/");

        RegisterResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.username()).isEqualTo("realuser");
        assertThat(body.email()).isEqualTo("real.user@university.edu");

        assertThat(userRepository.existsByUsername("realuser")).isTrue();
    }
}
