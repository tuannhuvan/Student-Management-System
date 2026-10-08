package org.example.demostudentmanagementsystem.service;

import org.example.demostudentmanagementsystem.audit.UserRegisteredEvent;
import org.example.demostudentmanagementsystem.dto.RegisterRequest;
import org.example.demostudentmanagementsystem.dto.RegisterResponse;
import org.example.demostudentmanagementsystem.entity.Role;
import org.example.demostudentmanagementsystem.entity.User;
import org.example.demostudentmanagementsystem.entity.UserStatus;
import org.example.demostudentmanagementsystem.exception.DuplicateResourceException;
import org.example.demostudentmanagementsystem.repository.UserRepository;
import org.example.demostudentmanagementsystem.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "johndoe",
                "john.doe@university.edu",
                "SecurePass123!",
                "John",
                "Doe"
        );
    }

    @Test
    @DisplayName("Should successfully register a new user when request payload is valid")
    void register_ValidRequest_Success() {
        // Given
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPasswordHash");

        User savedUser = User.builder()
                .id(1L)
                .username(registerRequest.username())
                .email(registerRequest.email())
                .passwordHash("encodedPasswordHash")
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .role(Role.ROLE_STUDENT)
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        RegisterResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("johndoe");
        assertThat(response.email()).isEqualTo("john.doe@university.edu");
        assertThat(response.role()).isEqualTo(Role.ROLE_STUDENT);

        verify(passwordEncoder).encode("SecurePass123!");
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        UserRegisteredEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.userId()).isEqualTo(1L);
        assertThat(capturedEvent.username()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void register_DuplicateEmail_ThrowsDuplicateResourceException() {
        // Given
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already in use")
                .extracting("errorCode")
                .isEqualTo("AUTH_EMAIL_ALREADY_EXISTS");

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when username already exists")
    void register_DuplicateUsername_ThrowsDuplicateResourceException() {
        // Given
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username is already taken")
                .extracting("errorCode")
                .isEqualTo("AUTH_USERNAME_ALREADY_EXISTS");

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
