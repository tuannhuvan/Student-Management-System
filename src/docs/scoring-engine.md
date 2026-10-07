# Scoring Engine & Academic Evaluation Rules

Đặc tả Động cơ Tính điểm (Scoring Engine) và Quy tắc Xếp loại Học tập dành cho Hệ thống Quản lý Sinh viên.

---

## 1. Công thức Tính Điểm Môn học (Course Grade Calculation)

Điểm tổng kết môn học (`total_score`) được tính theo tỷ lệ trọng số giữa Điểm Giữa Kỳ (`midterm_score`) và Điểm Cuối Kỳ (`final_score`):

$$\text{Total Score} = (\text{Midterm Score} \times 0.4) + (\text{Final Score} \times 0.6)$$

### Quy đổi Điểm Chữ & Thang điểm 4 (Grade Conversion Table)

| Điểm tổng kết (Thang 10) | Điểm Chữ (Letter Grade) | Thang điểm 4 (GPA Equivalent) | Xếp loại (Classification) |
| :---: | :---: | :---: | :--- |
| **8.5 - 10.0** | **A** | 4.0 | Xuất sắc / Giỏi (Excellent) |
| **7.0 - 8.4** | **B** | 3.0 | Khá (Good) |
| **5.5 - 6.9** | **C** | 2.0 | Trung bình (Average) |
| **4.0 - 5.4** | **D** | 1.0 | Yếu / Trung bình yếu (Below Average) |
| **< 4.0** | **F** | 0.0 | Kém / Trượt môn (Failed) |

---

## 2. Thuật toán Tính GPA Tích lũy (Cumulative GPA Engine)

Điểm trung bình tích lũy (GPA) được tính bằng tổng (Điểm thang 4 $\times$ Số tín chỉ) chia cho Tổng số tín chỉ đăng ký:

$$\text{GPA} = \frac{\sum_{i=1}^{n} (\text{GPA Equivalent}_i \times \text{Credits}_i)}{\sum_{i=1}^{n} \text{Credits}_i}$$

### Triển khai Logic trong Java Service

```java
@Service
public class ScoringEngineService {

    public ScoringResult calculateStudentGPA(List<Enrollment> enrollments) {
        double totalWeightedPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment e : enrollments) {
            if (e.getFinalScore() == null) continue; // Bỏ qua môn chưa có điểm kết thúc
            
            double totalScore = (e.getMidtermScore() * 0.4) + (e.getFinalScore() * 0.6);
            double gpaPoints = convertToGpaPoints(totalScore);
            int credits = e.getCourse().getCredits();

            totalWeightedPoints += gpaPoints * credits;
            totalCredits += credits;
        }

        double cumulativeGpa = (totalCredits == 0) ? 0.0 : Math.round((totalWeightedPoints / totalCredits) * 100.0) / 100.0;
        AcademicStatus status = determineAcademicStatus(cumulativeGpa);

        return new ScoringResult(cumulativeGpa, totalCredits, status);
    }

    private double convertToGpaPoints(double score) {
        if (score >= 8.5) return 4.0;
        if (score >= 7.0) return 3.0;
        if (score >= 5.5) return 2.0;
        if (score >= 4.0) return 1.0;
        return 0.0;
    }
}
```

---

## 3. Quy tắc Cảnh báo Học tập (Academic Status Rules)

- **Cảnh báo học tập mức 1:** GPA < 1.5 trong 1 học kỳ.
- **Buộc thôi học (Academic Dismissal):** GPA < 1.0 trong 2 học kỳ liên tiếp hoặc có trên 50% số tín chỉ bị điểm F.
