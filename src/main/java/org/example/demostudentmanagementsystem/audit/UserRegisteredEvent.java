package org.example.demostudentmanagementsystem.audit;

import java.time.Instant;

/**
 * Domain event published when a new user registers successfully.
 *
 * @param userId    Unique ID of the registered user
 * @param username  Username of the registered user
 * @param email     Email address of the registered user
 * @param timestamp Event creation timestamp
 */
public record UserRegisteredEvent(
    Long userId,
    String username,
    String email,
    Instant timestamp
) {
}
