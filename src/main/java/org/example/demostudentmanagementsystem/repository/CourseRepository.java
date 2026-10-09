package org.example.demostudentmanagementsystem.repository;

import org.example.demostudentmanagementsystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link Course} entity operations.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Checks if a course exists with the specified code.
     *
     * @param code Course code
     * @return true if course code exists
     */
    boolean existsByCode(String code);

    /**
     * Finds a course by code.
     *
     * @param code Course code
     * @return Optional containing course if found
     */
    Optional<Course> findByCode(String code);
}
