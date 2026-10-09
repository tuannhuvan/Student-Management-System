package org.example.demostudentmanagementsystem.service;

import org.example.demostudentmanagementsystem.dto.GpaSummaryResponse;
import org.example.demostudentmanagementsystem.dto.GradeSubmissionRequest;

/**
 * Service interface defining grading submission, letter grade calculation, and GPA computation.
 */
public interface GradeService {

    /**
     * Submits or updates a course grade for a student.
     *
     * @param request Grade submission payload
     */
    void submitGrade(GradeSubmissionRequest request);

    /**
     * Calculates cumulative GPA and academic standing for a student.
     *
     * @param studentId Student ID
     * @return GpaSummaryResponse containing calculated GPA and academic standing status
     */
    GpaSummaryResponse getStudentGpaSummary(Long studentId);
}
