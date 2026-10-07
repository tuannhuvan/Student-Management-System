# QA Strategy & Testing Standards

Chiến lược Kiểm thử Chất lượng Phần mềm (QA/QC) cho Dự án Spring Boot 3.

---

## 1. Tháp Kiểm thử (Testing Pyramid Criteria)

```
        /        /   \     E2E / API Tests (Postman / RestAssured) -> 10%
      /          /-------\   Integration Tests (Testcontainers) -> 30%
    /            /-----------\ Unit Tests (JUnit 5 + Mockito) -> 60%
```

---

## 2. Tiêu chuẩn Test Coverage

- **Overall Code Coverage:** $\ge 80\%$
- **Service Layer Coverage:** $\ge 90\%$
- **Controller & Repository Layer Coverage:** $\ge 75\%$

---

## 3. Định nghĩa Mức độ Lỗi (Defect Severity Classification)

| Severity | Criteria | Resolution SLA |
| :--- | :--- | :--- |
| **Critical / Blocker** | System crash, mất mát dữ liệu, lỗ hổng bảo mật nghiêm trọng. | Fix trong 4 giờ |
| **Major** | Tính năng cốt lõi (CRUD, Scoring) sai logic nhưng có workaround. | Fix trong 24 giờ |
| **Minor** | Lỗi hiển thị UI, sai định dạng thông báo. | Fix trong Sprint |
