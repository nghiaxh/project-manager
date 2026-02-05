# Hệ Thống Quản Lý Dự Án

Ứng dụng quản lý dự án quy mô vừa, hỗ trợ các nhóm làm việc cộng tác hiệu quả trong việc phân công, theo dõi và hoàn thành công việc.

## Giới Thiệu

Hệ thống được phát triển nhằm hỗ trợ các nhóm từ 3-20 người (sinh viên, startup, doanh nghiệp nhỏ) quản lý dự án một cách minh bạch và có tổ chức.

### Mục Tiêu

- Tái sử dụng cao (Design for Reuse)
- Dễ thích ứng với thay đổi (Design for Change)
- Dễ bảo trì và mở rộng (Maintainability & Extensibility)
- Áp dụng Design Patterns
- Lập trình đa luồng (Concurrency)
- Kiểm thử toàn diện

## Tính Năng Chính

**Quản lý dự án**: Tạo, chỉnh sửa, xóa dự án; phân quyền quản lý dự án

**Quản lý công việc**: Tạo task, gán người thực hiện, đặt deadline, cập nhật trạng thái (To Do, In Progress, Done, Cancel)

**Quản lý người dùng**: Đăng ký, đăng nhập, phân vai trò (Admin, Manager, Member)

**Hệ thống thông báo**: Thông báo real-time khi thay đổi trạng thái công việc hoặc deadline sắp đến

**Bình luận & trao đổi**: Thảo luận trong từng công việc

**Thống kê & báo cáo**: Số lượng công việc theo trạng thái, tiến độ dự án

## Kiến Trúc Hệ Thống

Hệ thống sử dụng kiến trúc Client-Server:

- **Client**: Tauri + Vue.js (Desktop application)
- **Server**: Java Spring Boot (RESTful API)
- **Database**: MySQL
- **Communication**: REST API

### Design Patterns

**Observer Pattern**: Hệ thống thông báo real-time khi có thay đổi trạng thái công việc

**Strategy Pattern**: Linh hoạt trong việc sắp xếp và lọc công việc (theo deadline, priority, status)

**Factory Pattern**: Tạo các đối tượng thông báo khác nhau (DeadlineNotification, StatusChangeNotification, AssignmentNotification)

**Template Method Pattern**: Quy trình tạo báo cáo (collectData, processData, formatReport, exportReport)

**Singleton Pattern**: Quản lý kết nối database và application configuration

## Yêu Cầu Hệ Thống

**Phần mềm cần thiết**:

- Node.js 21+
- Java JDK 25+
- MySQL 8.0+
- Maven 3.8+

## Cài Đặt

### 1. Clone Repository

```bash
git clone https://github.com/nghiaxh/project-manager.git
cd project-manager
```

### 2. Cấu Hình Database

Tạo database và import schema:

```bash
mysql -u root -p
CREATE DATABASE project_manager_db;
exit

mysql -u root -p project_manager_db < database/schema.sql
```

### 3. Cài Đặt và Chạy Server

```bash
cd server
mvn clean install
mvn spring-boot:run
```

Server chạy tại: `http://localhost:8080`

### 4. Cài Đặt và Chạy Client

```bash
cd client
npm install
npm run tauri dev
```

## Cấu Trúc Dự Án

```
project-management-system/
│
├── README.md
│
├── documents/
│   ├── requirements.docx          # Đặc tả yêu cầu
│   ├── design.docx                # Tài liệu thiết kế
│   └── diagrams/                  # UML diagrams
│       ├── use-case-diagram.png
│       ├── class-diagram.png
│       └── sequence-diagram.png
│
├── client/                        # Tauri + Vue.js
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── services/              # API calls
│   │   ├── store/                 # State management
│   │   └── router/
│   ├── src-tauri/                 # Tauri backend (Rust)
│   ├── package.json
│   └── vite.config.js
│
├── server/                        # Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/projectmanagement/
│   │   │   │   ├── controller/    # REST Controllers
│   │   │   │   ├── service/       # Business Logic
│   │   │   │   ├── repository/    # Data Access
│   │   │   │   ├── model/         # Entity Models
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── config/        # Configuration
│   │   │   │   └── util/          # Utilities
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                  # Unit & Integration Tests
│   ├── pom.xml
│   └── mvnw
│
└── database/
    ├── schema.sql                 # Database schema
    └── sample_data.sql            # Sample data
```

## API Endpoints

### Authentication

- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/logout` - Đăng xuất

### Projects

- `GET /api/projects` - Lấy danh sách dự án
- `POST /api/projects` - Tạo dự án mới
- `PUT /api/projects/{id}` - Cập nhật dự án
- `DELETE /api/projects/{id}` - Xóa dự án

### Tasks

- `GET /api/projects/{projectId}/tasks` - Lấy danh sách công việc
- `POST /api/projects/{projectId}/tasks` - Tạo công việc mới
- `PUT /api/tasks/{id}` - Cập nhật công việc
- `DELETE /api/tasks/{id}` - Xóa công việc

### Users

- `GET /api/users` - Lấy danh sách người dùng
- `GET /api/users/{id}` - Lấy thông tin người dùng
- `PUT /api/users/{id}` - Cập nhật thông tin

### Notifications

- `GET /api/notifications` - Lấy danh sách thông báo
- `PUT /api/notifications/{id}/read` - Đánh dấu đã đọc

### Statistics

- `GET /api/statistics/projects/{id}` - Thống kê dự án
- `GET /api/statistics/users/{id}` - Thống kê người dùng

## Kiểm Thử

### Server (Spring Boot)

```bash
cd server
mvn test                    # Unit tests
mvn verify                  # Integration tests
mvn jacoco:report           # Coverage report
```

### Client (Vue.js)

```bash
cd client
npm run test:unit           # Unit tests
npm run test:e2e            # E2E tests
```

## Công Nghệ Sử Dụng

**Client**:

- Tauri (Desktop framework)
- Vue.js 3 (JavaScript framework)
- Pinia (State management)
- Vue Router (Routing)
- Axios (HTTP client)
- Tailwind CSS (Styling)

**Server**:

- Spring Boot 3.x
- Spring Security (Authentication & Authorization)
- Spring Data JPA (ORM)
- MySQL Connector
- Lombok (Code generation)
- JUnit 5 (Testing)

**Database**:

- MySQL 8.0

## Nhóm Phát Triển

| STT | Họ và tên              | MSSV     | Vai trò     |
| --- | ---------------------- | -------- | ----------- |
| 1   | Đặng Hoàng Nghĩa       | B2303831 | Team Leader |
| 2   | Đàm Vĩnh Hưng          | B2303817 | Member      |
| 3   | Lâm Tuấn Hưng          | B2303818 | Member      |
| 4   | Đinh Hoàng Khâm        | B2303822 | Member      |
| 5   | Nguyễn Hữu Khôi Nguyên | B2303835 | Member      |
