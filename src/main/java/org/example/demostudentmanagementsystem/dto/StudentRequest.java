package org.example.demostudentmanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO record representing student creation/update request payload.
 *
 * @param firstName  Student first name
 * @param lastName   Student last name
 * @param email      Valid unique email address
 * @param department Academic department
 * @param dob        Date of birth (past or present)
 */
public record StudentRequest(
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    String department,

    @PastOrPresent(message = "Date of birth must be a date in the past or present")
    LocalDate dob
) {
}
