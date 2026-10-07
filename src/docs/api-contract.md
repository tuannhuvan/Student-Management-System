# API Contract Specification (RESTful API Standards)

Tài liệu Đặc tả Hợp đồng API (API Contract) chuẩn RESTful cho Dự án Quản lý Sinh viên. Tất cả API đều sử dụng định dạng JSON, phản hồi theo chuẩn RFC 7807 (Problem Details for HTTP APIs) khi xảy ra lỗi.

---

## 1. Danh sách Endpoints Chính

### 1.1. Lấy danh sách sinh viên (Phân trang & Tìm kiếm)
- **URL:** `GET /api/v1/students`
- **Query Parameters:**
  - `page` (int, default: 0): Số trang.
  - `size` (int, default: 10): Số lượng bản ghi/trang.
  - `keyword` (string, optional): Từ khóa tìm kiếm theo tên/mã sinh viên.
- **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "studentCode": "SV2026001",
      "fullName": "Nguyễn Văn A",
      "email": "nguyenvana@example.com",
      "departmentCode": "CNTT",
      "status": "ACTIVE"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### 1.2. Tạo mới sinh viên
- **URL:** `POST /api/v1/students`
- **Request Body:**
```json
{
  "studentCode": "SV2026002",
  "fullName": "Trần Thị B",
  "email": "tranthib@example.com",
  "dateOfBirth": "2004-05-15",
  "departmentId": 2
}
```
- **Response (201 Created):**
```json
{
  "id": 2,
  "studentCode": "SV2026002",
  "fullName": "Trần Thị B",
  "email": "tranthib@example.com",
  "departmentCode": "DTVT",
  "status": "ACTIVE"
}
```

---

## 2. Định dạng Lỗi Chuẩn RFC 7807 (Error Response Payload)

Mọi phản hồi lỗi (4xx, 5xx) tuân thủ cấu trúc:

```json
{
  "type": "https://api.example.com/errors/validation-failed",
  "title": "Bad Request",
  "status": 400,
  "detail": "Dữ liệu đầu vào không hợp lệ",
  "instance": "/api/v1/students",
  "timestamp": "2026-10-07T14:30:00Z",
  "errors": {
    "email": "Email phải đúng định dạng",
    "studentCode": "Mã sinh viên không được để trống"
  }
}
```

---

## 3. Bảng Mã Trạng thái HTTP (HTTP Status Codes)

| Status Code | Description | Usage Context |
| :---: | :--- | :--- |
| **200 OK** | Success | GET, PUT thành công |
| **201 Created** | Created | POST tạo mới tài nguyên thành công |
| **204 No Content** | No Content | DELETE thành công |
| **400 Bad Request** | Validation Failure | Dữ liệu gửi lên sai định dạng |
| **401 Unauthorized** | Unauthenticated | Thiếu hoặc vô hiệu JWT token |
| **403 Forbidden** | Access Denied | Không đủ quyền (Role) truy cập |
| **404 Not Found** | Resource Missing | Không tìm thấy ID sinh viên |
| **409 Conflict** | Duplicate Data | Trùng lặp Email hoặc Mã sinh viên |
| **500 Internal Server Error** | System Failure | Lỗi hệ thống không lường trước |
