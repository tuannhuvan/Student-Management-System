package org.example.demostudentmanagementsystem.dto;

import org.example.demostudentmanagementsystem.entity.Role;

import java.time.Instant;

/**
 * DTO record representing a successful user registration response payload.
 * Sensitive fields such as password hash are strictly excluded.
 *
 * @param id        Unique user ID
 * @param username  Registered username
 * @param email     Registered email address
 * @param firstName User first name
 * @param lastName  User last name
 * @param role      Assigned user role
 * @param createdAt Creation timestamp
 */
public record RegisterResponse(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    Role role,
    Instant createdAt
) {
}
