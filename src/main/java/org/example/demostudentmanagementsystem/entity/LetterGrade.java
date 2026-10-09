package org.example.demostudentmanagementsystem.entity;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Letter grade enumeration with 4.0 scale point mapping defined in scoring engine rules.
 */
@Getter
public enum LetterGrade {
    A(new BigDecimal("4.0")),
    B(new BigDecimal("3.0")),
    C(new BigDecimal("2.0")),
    D(new BigDecimal("1.0")),
    F(new BigDecimal("0.0"));

    private final BigDecimal gradePoints;

    LetterGrade(BigDecimal gradePoints) {
        this.gradePoints = gradePoints;
    }

    /**
     * Determines LetterGrade from a numeric score percentage (0.0 to 100.0).
     *
     * @param score Numeric score percentage
     * @return Corresponding LetterGrade enum
     */
    public static LetterGrade fromScore(BigDecimal score) {
        if (score == null) {
            return F;
        }
        double val = score.doubleValue();
        if (val >= 90.0) {
            return A;
        } else if (val >= 80.0) {
            return B;
        } else if (val >= 70.0) {
            return C;
        } else if (val >= 60.0) {
            return D;
        } else {
            return F;
        }
    }
}
