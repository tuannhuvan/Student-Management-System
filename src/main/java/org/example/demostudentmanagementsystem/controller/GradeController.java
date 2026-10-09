package org.example.demostudentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demostudentmanagementsystem.dto.GpaSummaryResponse;
import org.example.demostudentmanagementsystem.dto.GradeSubmissionRequest;
import org.example.demostudentmanagementsystem.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for grade submission and GPA computation endpoints.
 */
@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    /**
     * Submits or updates a student course grade. Requires ROLE_INSTRUCTOR or ROLE_ADMIN.
     *
     * @param request Validated grade submission request
     * @return ResponseEntity with HTTP 200 OK
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> submitGrade(@Valid @RequestBody GradeSubmissionRequest request) {
        gradeService.submitGrade(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves cumulative GPA summary and academic standing for a student.
     *
     * @param studentId Student ID
     * @return GpaSummaryResponse DTO
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('STUDENT')")
    public ResponseEntity<GpaSummaryResponse> getStudentGpaSummary(@PathVariable Long studentId) {
        GpaSummaryResponse response = gradeService.getStudentGpaSummary(studentId);
        return ResponseEntity.ok(response);
    }
}
