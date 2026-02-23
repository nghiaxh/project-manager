# Hệ Thống Quản Lý Dự Án

Ứng dụng quản lý dự án quy mô vừa, hỗ trợ các nhóm làm việc cộng tác hiệu quả trong việc phân công, theo dõi và hoàn thành công việc.

## Nhóm Phát Triển

| STT | Họ và tên              | MSSV     | Vai trò     |
| --- | ---------------------- | -------- | ----------- |
| 1   | Đặng Hoàng Nghĩa       | B2303831 | Team Leader |
| 2   | Đàm Vĩnh Hưng          | B2303817 | Member      |
| 3   | Lâm Tuấn Hưng          | B2303818 | Member      |
| 4   | Đinh Hoàng Khâm        | B2303822 | Member      |
| 5   | Nguyễn Hữu Khôi Nguyên | B2303835 | Member      |

## Cài Đặt

### 1. Clone Repository

```bash
git clone https://github.com/nghiaxh/project-manager.git
cd project-manager
```

### 2. Cấu Hình Server

```
spring.application.name=project-manager
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/project_manager
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=your-secret-key
jwt.expiration=86400000

cors.allowed-origins=http://localhost:1420,tauri://localhost
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true
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
