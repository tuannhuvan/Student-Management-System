package org.example.demostudentmanagementsystem.dto;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO record representing student response payload.
 *
 * @param id         Unique student ID
 * @param firstName  Student first name
 * @param lastName   Student last name
 * @param email      Student email
 * @param department Academic department
 * @param dob        Date of birth
 * @param createdAt  Creation timestamp
 */
public record StudentResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String department,
    LocalDate dob,
    Instant createdAt
) {
}
