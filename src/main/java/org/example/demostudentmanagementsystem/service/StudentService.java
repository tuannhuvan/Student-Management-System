package org.example.demostudentmanagementsystem.service;

import org.example.demostudentmanagementsystem.dto.StudentRequest;
import org.example.demostudentmanagementsystem.dto.StudentResponse;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing student business operations.
 */
public interface StudentService {

    /**
     * Creates a new student record.
     *
     * @param request Student creation payload
     * @return Created student response DTO
     */
    StudentResponse createStudent(StudentRequest request);

    /**
     * Retrieves student details by ID.
     *
     * @param id Student ID
     * @return Student response DTO
     */
    StudentResponse getStudentById(Long id);

    /**
     * Retrieves a paginated and filtered list of students.
     *
     * @param page    Page index (0-based)
     * @param size    Page size
     * @param sort    Sort field and direction (e.g. "id,asc")
     * @param keyword Search keyword matching name or email
     * @return Page of StudentResponse DTOs
     */
    Page<StudentResponse> getAllStudents(int page, int size, String sort, String keyword);

    /**
     * Updates an existing student record.
     *
     * @param id      Student ID to update
     * @param request Updated student payload
     * @return Updated student response DTO
     */
    StudentResponse updateStudent(Long id, StudentRequest request);

    /**
     * Removes a student record by ID.
     *
     * @param id Student ID to delete
     */
    void deleteStudent(Long id);
}
