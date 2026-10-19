# FastPOS Admin MVC và SQL Server

Bản 2.0 tổ chức lại phân hệ Admin bằng Java 17, Spring Boot 3.5.6, Spring MVC, Thymeleaf và Spring Data JPA. Giao diện dùng đúng bảng màu ảnh tham chiếu: sidebar `#1a1d29`, khối thương hiệu `#131722`, nền `#f8fafc`, màu nhấn `#e53935`, thẻ trắng. Không dùng theme xanh lá của bản trước.

17 bảng trong KLCN(1).sql được ánh xạ Entity với tên cột gốc. CRUD sử dụng Entity và JPA Repository thực sự. Các truy vấn doanh thu nằm ở Repository báo cáo, tách khỏi Controller và View.

## Chạy với database SQL Server của bạn

Ứng dụng mặc định dùng **SQL Server KLCN**, không tự chuyển sang database demo khi kết nối lỗi.

1. Nếu chưa có KLCN: mở SSMS, chạy `database/01-KLCN.sql`. File này giữ nguyên SQL bạn gửi, gồm schema và dữ liệu gốc. Nếu đã có KLCN, **không chạy lại script tạo database**.
2. Sao lưu database đang dùng rồi chạy `database/02-admin-extension.sql`: thêm bảng nhật ký, cột hết món tạm thời `MonAn.ConHang` và trạng thái đơn `DA_GIAO`. Script không xóa dữ liệu; có thể chạy lại trên bản 1.
3. Bật TCP/IP cho SQL Server, chọn đúng cổng và tài khoản kết nối. Trong PowerShell, tại thư mục đã giải nén:

```powershell
$env:DB_URL='jdbc:sqlserver://localhost:1433;databaseName=KLCN;encrypt=true;trustServerCertificate=true'
$env:DB_USER='sa'
$env:DB_PASSWORD='MAT_KHAU_SQL_SERVER'
# SQL gốc chứa hash giả. Lần đầu thay hash mẫu của riêng admin:
$env:FASTPOS_BOOTSTRAP_PASSWORD='DoiMatKhauAdmin@123'
java -jar dist/fastpos-admin.jar
```

Mở http://localhost:8080, đăng nhập `admin / DoiMatKhauAdmin@123`. Bootstrap chỉ đổi chuỗi hash mẫu `HASHADMIN000...`, không ghi đè hash BCrypt thật. Nếu đã có mật khẩu Admin hợp lệ thì dùng mật khẩu đó. Sau lần đầu bỏ biến bootstrap. Các tài khoản khác có hash mẫu cần được đặt mật khẩu mới từ trang quản lý tài khoản.

`sa` là SQL Login của SQL Server. Nếu SSMS đang dùng Windows Authentication, cấu hình SQL Login hoặc cấu hình riêng integrated authentication; mẫu trên dùng SQL Login. Các biến môi trường chỉ tồn tại trong cửa sổ PowerShell hiện tại. Không ghi mật khẩu thật vào git.

Hibernate dùng `ddl-auto=validate`, đối chiếu mapping và dừng khi thiếu bảng/cột. Ứng dụng không dùng `update/create/drop` để tự thay cấu trúc SQL Server. Giữ nguyên tên PascalCase, VARCHAR/NVARCHAR, BIGINT/INT, DECIMAL(18,2)/(18,3), DATETIME2 và khóa kép; không tự chuyển thành tên có dấu gạch dưới.

`trustServerCertificate=true` phù hợp môi trường phát triển; khi đưa lên máy chủ thật dùng chứng chỉ được tin cậy và HTTPS.

## Xem giao diện và demo nhanh

Cài Java 17+, giải nén toàn bộ thư mục rồi nhấp đúp `run-demo.bat`. Hoặc:

```powershell
java -jar dist/fastpos-admin.jar --spring.profiles.active=demo
```

Mở http://localhost:8080, tài khoản **admin / Admin@123**. Demo có sẵn dữ liệu món, combo, nguyên liệu và 8 đơn minh họa. Chỉ tạo dữ liệu khi database demo trống; lưu trong `data`. Đây là profile riêng, không phải SQL Server của bạn. Thay đổi được lưu sau khi tắt ứng dụng.

Chức năng bảo trì bật/tắt dùng được trong demo. Sao lưu `.bak`, khôi phục và CHECKDB bị khóa trong demo vì các lệnh này thuộc SQL Server; hệ thống không trả kết quả sao lưu giả.

## Chức năng đã có

- Đăng nhập, đăng xuất, BCrypt, quyền ADMIN, CSRF; khóa/đổi quyền/đổi mật khẩu làm phiên cũ mất hiệu lực ở yêu cầu tiếp theo.
- Dashboard theo ngày: doanh thu từng giờ, đơn hoàn tất, món đang bán, đơn cần chế biến và cảnh báo nguyên liệu từ TonKhoHienTai/NguongCanhBao. Không tạo dữ liệu ca làm hoặc trạng thái thiết bị giả khi SQL chưa có các bảng đó.
- Tài khoản, danh mục, món, combo, khuyến mãi: thêm/sửa/ngừng sử dụng và mở lại; giữ lịch sử tham chiếu.
- Món có trạng thái ngừng bán và hết món tạm thời riêng. Combo có món ngừng/hết hoặc danh mục ngừng sẽ có cảnh báo không thể bán.
- Đơn hàng, bộ lọc, chi tiết món/ combo/thanh toán; bếp vàng sau 3 phút, đỏ sau 7 phút; chỉ xử lý đơn đã trả tiền.
- Luồng chờ bếp → đang làm → hoàn tất chế biến → đã giao; khóa bản ghi đơn khi chuyển trạng thái và từ chối thao tác trên trạng thái cũ. Đơn chưa trả tiền có thể hủy kèm lý do, đơn đã trả tiền cần quy trình hoàn tiền riêng.
- WebSocket JSON cùng nguồn cập nhật sau transaction commit, tự kết nối lại và đồng bộ dự phòng mỗi 30 giây.
- Báo cáo, CSV, nhật ký Admin.
- Quản lý hệ thống: bảo trì, sao lưu full `.bak` với COPY_ONLY/CHECKSUM, kiểm tra bản sao, khôi phục, bản an toàn trước khôi phục và CHECKDB; có tác vụ nền và lịch sử bên ngoài DB cần khôi phục.

## Bật sao lưu và khôi phục SQL Server

Cần đọc `docs/SAO_LUU_KHOI_PHUC.md`. Tóm tắt cấu hình PowerShell, cùng cửa sổ đã đặt DB_URL/DB_USER/DB_PASSWORD:

```powershell
$env:BACKUP_ENABLED='true'
$env:DB_NAME='KLCN'
$env:SQL_BACKUP_DIR='C:/FastPOSBackups'
$env:DB_ADMIN_URL='jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true'
$env:DB_ADMIN_USER='TAI_KHOAN_QUAN_TRI_SAO_LUU'
$env:DB_ADMIN_PASSWORD='MAT_KHAU_TAI_KHOAN_SAO_LUU'
java -jar dist/fastpos-admin.jar --spring.profiles.active=sqlserver
```

Thư mục `C:/FastPOSBackups` phải tồn tại **trên máy chạy SQL Server**, tài khoản dịch vụ SQL Server phải có quyền đọc/ghi. Nếu SQL Server chạy Linux/container thì dùng đường dẫn được mount trên SQL Server, ví dụ `/var/opt/mssql/backup`. Đây không phải thư mục Downloads của trình duyệt.

Tài khoản quản trị kết nối `master`, có quyền BACKUP/RESTORE, ALTER DATABASE và DBCC CHECKDB cần thiết. Với đồ án local có thể dùng sysadmin; khi triển khai thật cần DBA cấp quyền phù hợp, không cấp tài khoản đặc quyền cho phần đặt món công khai. Tài khoản ứng dụng và tài khoản quản trị có thể khác nhau.

## Cấu trúc MVC

```text
src/main/java/vn/fastpos/admin/
  controller/   Nhận HTTP, chọn View và gọi Service; không viết SQL
  dto/          Request có validation; chuyển dữ liệu trả giao diện
  entity/       17 Entity theo SQL + NhatKyAdmin + 3 class khóa kép
  repository/   JPA Repository theo từng Entity và Repository báo cáo
  service/      Nghiệp vụ thực đơn, tài khoản, đơn hàng, báo cáo, hệ thống
  config/       Spring Security, WebSocket, dữ liệu demo
  system/       SQL Server backup engine, trạng thái bảo trì, gate, lịch sử tác vụ
  exception/    Xử lý lỗi nghiệp vụ và validation
src/main/resources/
  templates/admin/layout.html             View Thymeleaf chính
  templates/admin/fragments/sidebar.html   Menu dùng chung
  static/app.js                           Luồng thao tác, form và gọi REST
  static/js/dashboard.js                  Biểu đồ doanh thu
  static/js/system.js                     Giao diện sao lưu/khôi phục/bảo trì
  static/js/icons.js                      Icon SVG nội bộ
  static/styles.css                       Bảng màu và bố cục theo ảnh
```

MVC ở đây có View Thymeleaf cho khung trang và menu, JavaScript gọi REST để cập nhật nội dung mà không tải lại toàn trang. Các URL `/admin/dashboard`, `/admin/dishes`, `/admin/combos`, `/admin/system`... được Controller xử lý, nên truy cập trực tiếp hoặc F5 đều dùng được.

Ví dụ luồng lưu món: `app.js` → `CatalogController` nhận `MonAnRequest` → `ThucDonService.luuMonAn` kiểm tra nghiệp vụ → `MonAnRepository` → Entity MonAn → SQL Server → DTO giao diện. Chi tiết xem `docs/KIEN_TRUC_MVC.md`.

## Mở và build trong IntelliJ

Open thư mục chứa `pom.xml`, Project SDK 17, Load Maven rồi chạy `FastPosApplication`. Run Configuration mặc định SQL Server; thêm các Environment variables như trên. Muốn demo, thêm Program arguments `--spring.profiles.active=demo`.

Không dùng Lombok. Getters/setters và quan hệ Entity được viết rõ trong từng file. Máy có Maven 3.9+ có thể chạy:

```powershell
mvn clean test package
java -jar target/fastpos-admin-2.0.0.jar --spring.profiles.active=demo
```

## Giới hạn cần biết

Đây là phân hệ Admin, chưa xây lại ba giao diện kiosk/thu ngân/bếp độc lập. Chưa tích hợp thanh toán thật, hoàn tiền, FP-Growth, Redis, JWT hay STOMP. Admin hiện dùng session và WebSocket JSON. Danh sách đơn lấy 500 đơn mới nhất, có tra cứu mã đơn cũ; hàng đợi bếp có truy vấn riêng.

Khuyến mãi quản lý cấu hình; việc áp dụng ở service đặt đơn phải được tích hợp khi làm kiosk/thu ngân. Tên chương trình không tự tạo điều kiện khách mới/sinh viên/khung giờ. Giá bán đã chốt ở dòng đơn; tên và thành phần combo lịch sử còn cần snapshot khi hoàn thiện service đặt hàng.

Có Entity cho nguyên liệu, công thức và thuộc tính theo SQL, nhưng chưa thêm CRUD cho các nhóm này. Cảnh báo kho đọc số tồn hiện tại trong DB, không tự khẳng định đã có quy trình nhập/xuất hoặc trừ kho từ đơn hàng.

Bảo trì/gate trong bản này áp dụng cho **một tiến trình Spring Boot**. Nếu các phân hệ khác là ứng dụng riêng cùng ghi KLCN, cần dừng hoặc phối hợp chúng trước khi khôi phục; gate cục bộ không chặn ứng dụng khác. Không triển khai nhiều instance mà không bổ sung khóa phân tán và maintenance state dùng chung.

Đã kiểm thử với H2 và trình duyệt; các luồng tác vụ hệ thống có test với backup engine thay thế. Chưa có SQL Server thật trong môi trường tạo mã để chạy backup/restore end-to-end. Cần kiểm thử trên bản sao database trước khi vận hành.
