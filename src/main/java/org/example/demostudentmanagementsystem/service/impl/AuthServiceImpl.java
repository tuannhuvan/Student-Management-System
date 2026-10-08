package org.example.demostudentmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demostudentmanagementsystem.audit.UserRegisteredEvent;
import org.example.demostudentmanagementsystem.dto.RegisterRequest;
import org.example.demostudentmanagementsystem.dto.RegisterResponse;
import org.example.demostudentmanagementsystem.entity.Role;
import org.example.demostudentmanagementsystem.entity.User;
import org.example.demostudentmanagementsystem.entity.UserStatus;
import org.example.demostudentmanagementsystem.exception.DuplicateResourceException;
import org.example.demostudentmanagementsystem.repository.UserRepository;
import org.example.demostudentmanagementsystem.service.AuthService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Implementation of {@link AuthService} handling business logic for authentication and user registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Registers a new user account after validating uniqueness and encoding credentials.
     *
     * @param request Registration payload containing user details
     * @return RegisterResponse DTO with non-sensitive user profile data
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting user registration for username: {}, email: {}", request.username(), request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email {} is already registered", request.email());
            throw new DuplicateResourceException("Email is already in use", "AUTH_EMAIL_ALREADY_EXISTS");
        }

        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: Username {} is already taken", request.username());
            throw new DuplicateResourceException("Username is already taken", "AUTH_USERNAME_ALREADY_EXISTS");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(encodedPassword)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.ROLE_STUDENT)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("User registered successfully with ID: {}, username: {}", savedUser.getId(), savedUser.getUsername());

        eventPublisher.publishEvent(new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                Instant.now()
        ));

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }
}
