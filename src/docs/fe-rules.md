# Frontend Integration Guidelines (FE Rules)

Quy chuẩn Tích hợp Frontend (React / Angular / Vue) với Spring Boot 3 RESTful API.

---

## 1. Quản lý API Client & Interceptors

Frontend phải tạo một HTTP Client tập trung (ví dụ: Axios instance) để xử lý JWT Token và Error Handling:

```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Đính kèm Bearer Token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response Interceptor: Xử lý Lỗi Tập trung
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      window.location.href = '/login';
    }
    return Promise.reject(error.response?.data);
  }
);
```

---

## 2. Quy tắc Đồng bộ Phân trang (Pagination Standard)

- Spring Boot 3 sử dụng Phân trang Zero-Based Index (`page=0` là trang đầu tiên).
- FE UI Component (ví dụ Ant Design / Material UI) thường dùng 1-Based Index (`page=1`).
- FE có trách nhiệm giảm 1 unit khi gửi API request (`pageIndex - 1`) và cộng 1 unit khi nhận response.
