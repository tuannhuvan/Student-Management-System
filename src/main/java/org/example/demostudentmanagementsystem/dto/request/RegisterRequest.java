package org.example.demostudentmanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO record representing a user registration request payload.
 *
 * @param username  Unique username (4-30 chars, alphanumeric and underscores)
 * @param email     Valid email address
 * @param password  Password meeting complexity requirements
 * @param firstName Optional first name (max 50 chars)
 * @param lastName  Optional last name (max 50 chars)
 */
public record RegisterRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only letters, numbers, and underscores")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    String password,

    @Size(max = 50, message = "First name must not exceed 50 characters")
    String firstName,

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    String lastName
) {
}
