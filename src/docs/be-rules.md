# Backend Coding Standards & Guidelines (Spring Boot 3 / Java 17)

Tập hợp Quy chuẩn Viết mã nguồn Backend cho Dự án Spring Boot 3 & Java 17.

---

## 1. Chuẩn Hóa Java 17 Features & Clean Code

1. **Sử dụng Java 17 Record làm DTOs:**
   - Mọi Request/Response DTOs phải dùng `record` thay vì class với Lombok `@Data`.
   - Lợi ích: Bất biến (Immutable), tự động tạo getters, equals, hashCode, toString.

```java
public record StudentRequest(
    @NotBlank(message = "Mã sinh viên không được trống")
    String studentCode,

    @NotBlank(message = "Họ tên không được trống")
    String fullName,

    @Email(message = "Email không hợp lệ")
    String email,

    @NotNull
    Long departmentId
) {}
```

2. **Tiêm Phụ thuộc (Constructor Injection):**
   - **CẤM:** Sử dụng `@Autowired` trên field.
   - **BẮT BUỘC:** Sử dụng Constructor Injection (hoặc `@RequiredArgsConstructor` của Lombok với `final` fields).

```java
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
}
```

---

## 2. Quản lý Giao dịch (Transaction Management)

- Mọi phương thức đọc dữ liệu phải đánh dấu `@Transactional(readOnly = true)` để tối ưu hóa hiệu năng Hibernate/JPA.
- Phương thức ghi/sửa dữ liệu dùng `@Transactional` mặc định.
- Tắt Open Session in View (`spring.jpa.open-in-view=false`) để quản lý kết nối CSDL chặt chẽ.

---

## 3. Xử lý Lỗi Tập trung (Global Exception Handling)

Tất cả Exception đều được bắt tại `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        return problem;
    }
}
```
