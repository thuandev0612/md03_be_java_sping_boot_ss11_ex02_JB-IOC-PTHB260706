# Session11 - Bài 1 và 2: SLF4J, Lombok và cấu hình cấp độ log

Java 21, Spring Boot, JPA và PostgreSQL. Controller và Service dùng `@Slf4j` để ghi log trên console.

## Chạy ứng dụng

1. Mở project trong IntelliJ, chọn JDK 21 và tải lại Gradle.
2. Chạy `BaitapSession11Application`.
3. Mở cửa sổ Run/Console, rồi gọi API bằng Postman.

Spring Boot Web đã cung cấp SLF4J và Logback. build.gradle khai báo Lombok bằng compileOnly và annotationProcessor; annotation `@Slf4j` tự tạo biến logger tên `log`.

Database `medical_session11` đã được chuẩn bị trên máy hiện tại; mật khẩu nằm ngoài project tại `../.local-config/baitapSession11.properties`. Máy khác cần tạo database bằng `CREATE DATABASE medical_session11;` rồi đặt DB_USERNAME và DB_PASSWORD. DB_URL cho phép đổi URL kết nối. Hibernate tự tạo bảng patients khi khởi động.

## Thử API

Chọn **POST → Body → raw → JSON**:

```text
POST http://localhost:8080/api/v1/patients
Content-Type: application/json
```

```json
{
  "name": "Nguyen Van An",
  "age": 30
}
```

API trả HTTP 200 và bệnh nhân đã lưu trong PostgreSQL, gồm id, name, age. Console xuất hiện hai log INFO từ Controller và Service:

```text
INFO ... PatientController : Nhận yêu cầu thêm bệnh nhân: Nguyen Van An
INFO ... PatientService    : Đã tiếp nhận bệnh nhân: Nguyen Van An
```

Đổi age thành 121 rồi gửi lại. Console có thêm log WARN:

```text
WARN ... PatientService : Tuổi bệnh nhân quá cao: name=Nguyen Van An, age=121
```

Điều kiện là **age > 120**: tuổi 120 không tạo WARN; tuổi 121 tạo WARN và vẫn được tiếp nhận với HTTP 200. Tên không được trống, tuổi phải được truyền và không âm; dữ liệu sai trả HTTP 400.

Ví dụ câu lệnh ghi log:

```java
log.info("Nhận yêu cầu thêm bệnh nhân: {}", patient.getName());
log.warn("Tuổi bệnh nhân quá cao: name={}, age={}", patient.getName(), patient.getAge());
```

## Bài 2: Cấp độ log và SQL trên console

application.properties cấu hình:

```properties
logging.level.root=INFO
logging.level.org.hibernate=DEBUG
logging.level.com.medical.service=TRACE
```

- Root INFO: hiển thị INFO, WARN và ERROR cho các logger không có mức riêng.
- Hibernate DEBUG: hiển thị DEBUG trở lên, bao gồm câu SQL từ logger org.hibernate.SQL.
- Package com.medical.service TRACE: hiển thị cả TRACE và DEBUG của PatientService.

PatientService được đặt trong package com.medical.service đúng yêu cầu; main quét cả package này và com.example.baitapsession11. spring.jpa.show-sql=false để câu SQL đi qua hệ thống logging và được lọc theo cấp độ; hibernate.format_sql=true giúp SQL dễ đọc.

Sau khi thêm bệnh nhân, gọi API tìm kiếm:

```text
GET http://localhost:8080/api/v1/patients?name=An
```

Tìm gần đúng theo tên, không phân biệt hoa/thường. Bỏ name sẽ lấy tất cả bệnh nhân. Console có dạng:

```text
INFO  ... PatientController : Nhận yêu cầu tìm bệnh nhân: An
TRACE ... PatientService    : Tìm kiếm bệnh nhân với name=An
DEBUG ... org.hibernate.SQL : select ... from patients ... where upper(...) like upper(?) ...
DEBUG ... PatientService    : Tìm thấy 1 bệnh nhân
```

TRACE chi tiết nhất, tiếp theo là DEBUG, INFO, WARN và ERROR. Mỗi mức cho phép hiển thị chính mức đó và các mức nghiêm trọng hơn.

Nếu muốn chỉ xem lỗi ở môi trường chạy thực tế, đổi cả ba cấu hình:

```properties
logging.level.root=ERROR
logging.level.org.hibernate=ERROR
logging.level.com.medical.service=ERROR
```

Cần đổi các logger có mức riêng vì chúng ưu tiên hơn root. Khởi động lại ứng dụng sau khi sửa properties.

Build/cache được đặt ngoài project khi kiểm tra để giữ ít file nộp GitHub. Nếu IntelliJ tạo thêm build/.gradle/.idea thì bỏ các thư mục đó khi kéo thả lên GitHub.
