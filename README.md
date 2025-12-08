# Quan Ly Diem (QLDIEM) - Hệ thống quản lý điểm sinh viên



## Mục lục
1. [Fork repository](#fork-repository)
2. [Hiểu về mẫu MVC](#hiểu-về-mẫu-mvc)
3. [Thiết lập dự án](#thiết-lập-dự-án)
4. [Thay đổi theo mô hình MVC](#thay-đổi-theo-mô-hình-mvc)
5. [Gửi Pull Requests](#gửi-pull-requests)
6. [Cấu trúc dự án](#cấu-trúc-dự-án)

## Fork repository

1. **Truy cập repository**: Truy cập vào repository GitHub mà bạn muốn đóng góp.

2. **Fork repository**:
   - Nhấn vào nút "Fork" ở góc trên bên phải của trang repository.
   - Chọn tài khoản GitHub của bạn khi được yêu cầu.
   - Chờ GitHub tạo bản sao repository của bạn.

3. **Clone bản fork về máy cục bộ**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/QLDIEM.git
   cd QLDIEM
   ```

4. **Thiết lập remote upstream** (để giữ cho bản fork được đồng bộ):
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/QLDIEM.git
   ```

5. **Xác minh remote**:
   ```bash
   git remote -v
   ```

## Hiểu về mẫu MVC

Mẫu Model-View-Controller (MVC) chia ứng dụng thành ba thành phần liên kết với nhau, thúc đẩy sự tách biệt các mối quan tâm:

### Model
- Chứa logic kinh doanh và dữ liệu của ứng dụng.
- Đại diện cho cấu trúc dữ liệu và xử lý các hoạt động liên quan đến dữ liệu.
- Tương tác với cơ sở dữ liệu hoặc các dịch vụ bên ngoài.
- Ví dụ: `Student.java`, `Grade.java`, `DatabaseConnection.java`

### View
- Quản lý lớp trình bày của ứng dụng.
- Hiển thị dữ liệu cho người dùng và xử lý các thành phần giao diện người dùng.
- Nên độc lập với logic kinh doanh.
- Ví dụ: Các thành phần giao diện Swing, trang HTML hoặc định dạng đầu ra giao diện dòng lệnh

### Controller
- Đóng vai trò trung gian giữa Model và View.
- Xử lý đầu vào của người dùng và quản lý luồng dữ liệu giữa Model và View.
- Xử lý yêu cầu và điều phối hoạt động giữa Model và View.
- Ví dụ: `StudentController.java`, `GradeController.java`


## Thay đổi theo mô hình MVC

Khi đóng góp cho dự án này, vui lòng tuân theo mẫu kiến trúc MVC:

### Tạo tính năng mới

1. **Lớp Model**: Tạo lớp model mới hoặc cải tiến những lớp hiện có.
   - Đặt tất cả các lớp model vào gói `model`
   - Tuân thủ đóng gói đúng cách với các trường private và các getter/setter công khai
   - Bao gồm logic xác thực trong các lớp model khi cần thiết

2. **Lớp View**: Tạo hoặc cập nhật các thành phần giao diện người dùng.
   - Đặt tất cả các lớp view vào gói `view`
   - Giữ cho các view đơn giản và tránh triển khai logic kinh doanh ở đây
   - Thiết kế các view để giao tiếp với các controller

3. **Lớp Controller**: Tạo các lớp controller để quản lý luồng giữa các model và view.
   - Đặt tất cả các lớp controller vào gói `controller`
   - Triển khai các phương thức để xử lý đầu vào của người dùng và điều phối các tương tác giữa model và view
   - Tuân theo Nguyên tắc Trách nhiệm Đơn (Single Responsibility Principle)



## Gửi Pull Requests

### Trước khi tạo Pull Request

1. **Cập nhật kho lưu trữ cục bộ của bạn**:
   ```bash
   # Chuyển sang nhánh chính
   git checkout main

   # Lấy các thay đổi mới nhất từ upstream
   git fetch upstream

   # Hợp nhất các thay đổi từ upstream
   git merge upstream/main

   # Đẩy các cập nhật lên bản fork của bạn
   git push origin main
   ```

2. **Tạo một nhánh mới**:
   ```bash
   # Tạo và chuyển sang nhánh mới
   git checkout -b feature-descriptive-branch-name

   # Đối với sửa lỗi
   git checkout -b fix-descriptive-fix-name
   ```

3. **Thực hiện các thay đổi của bạn**:
   - Tuân theo mẫu MVC như đã mô tả ở trên
   - Viết các thông báo commit rõ ràng, ngắn gọn
   - Viết các bài kiểm thử cho chức năng mới

4. **Kiểm thử các thay đổi của bạn**:
   - Chạy các bài kiểm thử hiện có để đảm bảo không có gì bị hỏng
   - Thêm các bài kiểm thử mới nếu cần
   - Xác minh rằng các thay đổi của bạn hoạt động như mong đợi

5. **Cam kết các thay đổi của bạn**:
   ```bash
   # Kiểm tra các tập tin đã thay đổi
   git status

   # Thêm các tập tin cụ thể
   git add path/to/changed/files

   # Hoặc thêm tất cả các thay đổi (cẩn thận với điều này)
   git add .

   # Cam kết với một thông báo mô tả
   git commit -m "Thêm mô tả chi tiết về các thay đổi"
   ```

6. **Đẩy các thay đổi của bạn**:
   ```bash
   git push origin feature-descriptive-branch-name
   ```

### Tạo Pull Request

1. **Truy cập vào bản fork của bạn**: Truy cập vào kho lưu trữ bản fork của bạn trên GitHub.

2. **Chuyển sang nhánh của bạn**: Chọn nhánh nơi bạn đã thực hiện các thay đổi.

3. **Nhấn vào "New Pull Request"**: GitHub sẽ phát hiện các thay đổi và đề xuất tạo một pull request mới.

4. **Điền vào Pull Request**:
   - **Tiêu đề**: Viết một tiêu đề rõ ràng, mô tả (tối đa 50 ký tự)
   - **Mô tả**: Bao gồm thông tin chi tiết về các thay đổi của bạn:
     - Lý do các thay đổi này là cần thiết
     - Các thay đổi cụ thể đã được thực hiện
     - Các thay đổi tuân theo mẫu MVC như thế nào
     - Các thay đổi ảnh hưởng đến ứng dụng như thế nào
     - Bất kỳ thông tin bổ sung nào người xem xét nên biết

5. **Gửi Pull Request**: Nhấn vào "Create Pull Request"

### Hướng dẫn Pull Request

- **Tiêu đề mô tả**: Tóm tắt các thay đổi trong 50 ký tự hoặc ít hơn
- **Mô tả chi tiết**: Giải thích những gì và tại sao, không phải là cách
- **Tuân thủ mẫu MVC**: Đảm bảo tất cả các thay đổi phù hợp với kiến trúc MVC
- **Tham chiếu các vấn đề**: Nếu áp dụng, tham chiếu các vấn đề liên quan (#số-vấn-đề)
- **Giữ PR nhỏ**: Tập trung vào việc giải quyết một vấn đề tại một thời điểm
- **Cam kết sạch**: Đảm bảo các cam kết tuân theo định dạng cam kết quy ước
- **Cập nhật tài liệu**: Cập nhật README hoặc tài liệu khác nếu cần


## Cấu trúc dự án

```
QLDIEM/
├── pom.xml              # File cấu hình Maven
├── README.md            # Tập tin này
├── src/
│   ├── main/
│   │   └── java/
│   │           └── mvc/
│   │               ├── model/      # Các lớp của tầng Model
│   │               ├── view/       # Các lớp của tầng View
│   │               └── controller/ # Các lớp của tầng Controller



