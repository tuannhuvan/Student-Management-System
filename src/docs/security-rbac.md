# Security & Role-Based Access Control (RBAC) Specification

Đặc tả Kiến trúc Sercurity và Phân quyền theo Vai trò (RBAC) cho Hệ thống Quản lý Sinh viên dựa trên Spring Security 6 và JWT.

---

## 1. Mô hình Vai trò & Quyền hạn (Roles & Permissions Matrix)

Hệ thống định nghĩa 3 vai trò chính (`Role`):
- `ROLE_ADMIN`: Quản trị viên hệ thống.
- `ROLE_TEACHER`: Giảng viên / Cố vấn học tập.
- `ROLE_STUDENT`: Sinh viên.

### Ma trận Quyền hạn (Permissions Matrix)

| Endpoint / Action | Method | Public | ROLE_STUDENT | ROLE_TEACHER | ROLE_ADMIN |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `/api/v1/auth/login` | POST | ✅ | ✅ | ✅ | ✅ |
| `/swagger-ui/**`, `/v3/api-docs/**` | GET | ✅ | ✅ | ✅ | ✅ |
| `/actuator/health` | GET | ✅ | ✅ | ✅ | ✅ |
| `/api/v1/students/me` | GET | ❌ | ✅ (Chỉ xem bản thân) | ✅ | ✅ |
| `/api/v1/students` | GET | ❌ | ❌ | ✅ | ✅ |
| `/api/v1/students` | POST | ❌ | ❌ | ❌ | ✅ |
| `/api/v1/students/{id}` | PUT | ❌ | ❌ | ❌ | ✅ |
| `/api/v1/students/{id}` | DELETE | ❌ | ❌ | ❌ | ✅ |
| `/api/v1/scores/**` | GET/POST | ❌ | ✅ (Xem điểm cá nhân) | ✅ (Nhập/Sửa điểm) | ✅ |

---

## 2. Luồng Xác thực JWT (JWT Authentication Flow)

1. Client gửi credentials `(username, password)` đến `/api/v1/auth/login`.
2. `AuthenticationManager` của Spring Security xác thực thông tin đăng nhập qua `UserDetailsService`.
3. Nếu hợp lệ, hệ thống tạo `JWT Access Token` (thời hạn 1 giờ) và `Refresh Token` (thời hạn 7 ngày) chứa claims: `sub` (username), `roles`, `iat`, `exp`.
4. Với các request tiếp theo, Client đính kèm Header: `Authorization: Bearer <JWT_TOKEN>`.
5. `JwtAuthenticationFilter` giải mã token, thiết lập `SecurityContextHolder.getContext().setAuthentication(authentication)`.

---

## 3. Cấu hình Spring Security 6 (SecurityFilterChain)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/students/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/v1/students/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

## 4. Bảo mật Tầng Phương thức (Method-Level Security)

Sử dụng annotation `@PreAuthorize` để kiểm soát truy cập chi tiết đến từng hàm nghiệp vụ:

```java
@Service
public class StudentService {

    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse createStudent(StudentRequest request) { ... }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and #studentCode == authentication.name)")
    public StudentResponse getStudentByCode(String studentCode) { ... }
}
```
