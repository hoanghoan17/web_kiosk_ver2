CREATE DATABASE KLCN;
GO

USE KLCN;
GO


-------------------------- 1. VAI TRO ------------------

CREATE TABLE VaiTro
(
    MaVaiTro INT IDENTITY(1,1) PRIMARY KEY,
    TenVaiTro NVARCHAR(50) NOT NULL UNIQUE,
    MoTa NVARCHAR(255) NULL
);
GO


-------------------------- 2. TAI KHOAN ------------------

CREATE TABLE TaiKhoan
(
    MaTaiKhoan INT IDENTITY(1,1) PRIMARY KEY,
    MaVaiTro INT NOT NULL,
    TenDangNhap VARCHAR(100) NOT NULL UNIQUE,
    MatKhauHash VARCHAR(255) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    Email VARCHAR(150) NULL,
    SoDienThoai VARCHAR(20) NULL,
    TrangThai BIT NOT NULL DEFAULT 1,
    NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    NgayCapNhat DATETIME2 NULL,

    CONSTRAINT FK_TaiKhoan_VaiTro
        FOREIGN KEY (MaVaiTro)
        REFERENCES VaiTro(MaVaiTro)
);
GO


-------------------------- 3. KHACH HANG ------------------

CREATE TABLE KhachHang
(
    MaKhachHang INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NULL,
    SoDienThoai VARCHAR(20) NULL,
    Email VARCHAR(150) NULL,
    NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    TrangThai BIT NOT NULL DEFAULT 1
);
GO


-------------------------- 4. PHIEN DANG NHAP KIOSK ------------------

CREATE TABLE PhienDangNhapKiosk
(
    MaPhien BIGINT IDENTITY(1,1) PRIMARY KEY,
    MaKhachHang INT NULL,
    MaKiosk VARCHAR(50) NOT NULL,
    ThoiGianBatDau DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    ThoiGianKetThuc DATETIME2 NULL,
    TrangThai VARCHAR(30) NOT NULL,

    CONSTRAINT FK_PhienKiosk_KhachHang
        FOREIGN KEY (MaKhachHang)
        REFERENCES KhachHang(MaKhachHang),

    CONSTRAINT CK_PhienKiosk_TrangThai
        CHECK (TrangThai IN
        ('DANG_HOAT_DONG',
         'DA_KET_THUC',
         'HET_HAN'))
);
GO


-------------------------- 5. DANH MUC MON AN ------------------

CREATE TABLE DanhMucMonAn
(
    MaDanhMuc INT IDENTITY(1,1) PRIMARY KEY,
    TenDanhMuc NVARCHAR(100) NOT NULL UNIQUE,
    MoTa NVARCHAR(500) NULL,
    TrangThai BIT NOT NULL DEFAULT 1
);
GO


-------------------------- 6. MON AN ------------------

CREATE TABLE MonAn
(
    MaMonAn INT IDENTITY(1,1) PRIMARY KEY,
    MaDanhMuc INT NOT NULL,
    TenMon NVARCHAR(150) NOT NULL,
    MoTa NVARCHAR(500) NULL,
    GiaBan DECIMAL(18,2) NOT NULL,
    HinhAnh VARCHAR(500) NULL,
    TrangThai BIT NOT NULL DEFAULT 1,
    NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    NgayCapNhat DATETIME2 NULL,

    CONSTRAINT FK_MonAn_DanhMuc
        FOREIGN KEY (MaDanhMuc)
        REFERENCES DanhMucMonAn(MaDanhMuc),

    CONSTRAINT CK_MonAn_GiaBan
        CHECK (GiaBan >= 0)
);
GO


-------------------------- 7. THUOC TINH ------------------

CREATE TABLE ThuocTinh
(
    MaThuocTinh INT IDENTITY(1,1) PRIMARY KEY,
    TenThuocTinh NVARCHAR(100) NOT NULL UNIQUE,
    MoTa NVARCHAR(500) NULL
);
GO


-------------------------- 8. MON AN - THUOC TINH ------------------

CREATE TABLE MonAn_ThuocTinh
(
    MaMonAn INT NOT NULL,
    MaThuocTinh INT NOT NULL,
    GiaTri NVARCHAR(100) NOT NULL,

    CONSTRAINT PK_MonAn_ThuocTinh
        PRIMARY KEY (MaMonAn, MaThuocTinh),

    CONSTRAINT FK_MonAnThuocTinh_MonAn
        FOREIGN KEY (MaMonAn)
        REFERENCES MonAn(MaMonAn),

    CONSTRAINT FK_MonAnThuocTinh_ThuocTinh
        FOREIGN KEY (MaThuocTinh)
        REFERENCES ThuocTinh(MaThuocTinh)
);
GO


-------------------------- 9. COMBO ------------------

CREATE TABLE Combo
(
    MaCombo INT IDENTITY(1,1) PRIMARY KEY,
    TenCombo NVARCHAR(150) NOT NULL,
    MoTa NVARCHAR(500) NULL,
    GiaBan DECIMAL(18,2) NOT NULL,
    HinhAnh VARCHAR(500) NULL,
    TrangThai BIT NOT NULL DEFAULT 1,
    NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    NgayCapNhat DATETIME2 NULL,

    CONSTRAINT CK_Combo_GiaBan
        CHECK (GiaBan >= 0)
);
GO


-------------------------- 10. CHI TIET COMBO ------------------

CREATE TABLE ChiTietCombo
(
    MaCombo INT NOT NULL,
    MaMonAn INT NOT NULL,
    SoLuong INT NOT NULL,

    CONSTRAINT PK_ChiTietCombo
        PRIMARY KEY (MaCombo, MaMonAn),

    CONSTRAINT FK_ChiTietCombo_Combo
        FOREIGN KEY (MaCombo)
        REFERENCES Combo(MaCombo),

    CONSTRAINT FK_ChiTietCombo_MonAn
        FOREIGN KEY (MaMonAn)
        REFERENCES MonAn(MaMonAn),

    CONSTRAINT CK_ChiTietCombo_SoLuong
        CHECK (SoLuong > 0)
);
GO


-------------------------- 11. KHUYEN MAI ------------------

CREATE TABLE KhuyenMai
(
    MaKhuyenMai INT IDENTITY(1,1) PRIMARY KEY,
    MaCode VARCHAR(50) NOT NULL UNIQUE,
    TenKhuyenMai NVARCHAR(150) NOT NULL,
    MoTa NVARCHAR(500) NULL,

    LoaiGiam VARCHAR(20) NOT NULL,

    GiaTriGiam DECIMAL(18,2) NOT NULL,
    GiaTriDonToiThieu DECIMAL(18,2) NULL,
    GiamToiDa DECIMAL(18,2) NULL,

    NgayBatDau DATETIME2 NOT NULL,
    NgayKetThuc DATETIME2 NOT NULL,

    TrangThai BIT NOT NULL DEFAULT 1,

    CONSTRAINT CK_KhuyenMai_LoaiGiam
        CHECK (LoaiGiam IN ('PHAN_TRAM', 'SO_TIEN')),

    CONSTRAINT CK_KhuyenMai_GiaTri
        CHECK (GiaTriGiam >= 0),

    CONSTRAINT CK_KhuyenMai_ThoiGian
        CHECK (NgayKetThuc >= NgayBatDau)
);
GO


-------------------------- 12. DON HANG ------------------

CREATE TABLE DonHang
(
    MaDonHang BIGINT IDENTITY(1,1) PRIMARY KEY,

    MaKhachHang INT NULL,
    MaPhienKiosk BIGINT NULL,
    MaTaiKhoan INT NULL,
    MaKhuyenMai INT NULL,

    NguonDon VARCHAR(20) NOT NULL,
    TrangThaiDon VARCHAR(30) NOT NULL,

    ThoiGianDat DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    ThoiGianThanhToan DATETIME2 NULL,
    ThoiGianHoanThanh DATETIME2 NULL,

    TongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
    TienGiam DECIMAL(18,2) NOT NULL DEFAULT 0,
    ThanhTien DECIMAL(18,2) NOT NULL DEFAULT 0,

    GhiChu NVARCHAR(500) NULL,

    CONSTRAINT FK_DonHang_KhachHang
        FOREIGN KEY (MaKhachHang)
        REFERENCES KhachHang(MaKhachHang),

    CONSTRAINT FK_DonHang_PhienKiosk
        FOREIGN KEY (MaPhienKiosk)
        REFERENCES PhienDangNhapKiosk(MaPhien),

    CONSTRAINT FK_DonHang_TaiKhoan
        FOREIGN KEY (MaTaiKhoan)
        REFERENCES TaiKhoan(MaTaiKhoan),

    CONSTRAINT FK_DonHang_KhuyenMai
        FOREIGN KEY (MaKhuyenMai)
        REFERENCES KhuyenMai(MaKhuyenMai),

    CONSTRAINT CK_DonHang_Nguon
        CHECK (NguonDon IN ('KIOSK', 'COUNTER')),

    CONSTRAINT CK_DonHang_TrangThai
        CHECK (TrangThaiDon IN
        ('CHO_THANH_TOAN',
         'DA_THANH_TOAN',
         'CHO_CHE_BIEN',
         'DANG_CHE_BIEN',
         'HOAN_THANH',
         'DA_HUY')),

    CONSTRAINT CK_DonHang_Tien
        CHECK
        (
            TongTien >= 0
            AND TienGiam >= 0
            AND ThanhTien >= 0
        )
);
GO


-------------------------- 13. CHI TIET DON HANG - MON AN ------------------

CREATE TABLE ChiTietDonHang
(
    MaChiTietDonHang BIGINT IDENTITY(1,1) PRIMARY KEY,

    MaDonHang BIGINT NOT NULL,
    MaMonAn INT NOT NULL,

    SoLuong INT NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien DECIMAL(18,2) NOT NULL,

    CONSTRAINT FK_ChiTietDonHang_DonHang
        FOREIGN KEY (MaDonHang)
        REFERENCES DonHang(MaDonHang),

    CONSTRAINT FK_ChiTietDonHang_MonAn
        FOREIGN KEY (MaMonAn)
        REFERENCES MonAn(MaMonAn),

    CONSTRAINT CK_ChiTietDonHang_SoLuong
        CHECK (SoLuong > 0),

    CONSTRAINT CK_ChiTietDonHang_Gia
        CHECK (DonGia >= 0 AND ThanhTien >= 0),

    CONSTRAINT UQ_ChiTietDonHang_Mon
        UNIQUE (MaDonHang, MaMonAn)
);
GO


-------------------------- 14. CHI TIET DON HANG - COMBO ------------------

CREATE TABLE ChiTietDonHangCombo
(
    MaChiTietDonHangCombo BIGINT IDENTITY(1,1) PRIMARY KEY,

    MaDonHang BIGINT NOT NULL,
    MaCombo INT NOT NULL,

    SoLuong INT NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien DECIMAL(18,2) NOT NULL,

    CONSTRAINT FK_ChiTietDHCombo_DonHang
        FOREIGN KEY (MaDonHang)
        REFERENCES DonHang(MaDonHang),

    CONSTRAINT FK_ChiTietDHCombo_Combo
        FOREIGN KEY (MaCombo)
        REFERENCES Combo(MaCombo),

    CONSTRAINT CK_ChiTietDHCombo_SoLuong
        CHECK (SoLuong > 0),

    CONSTRAINT CK_ChiTietDHCombo_Gia
        CHECK (DonGia >= 0 AND ThanhTien >= 0),

    CONSTRAINT UQ_ChiTietDHCombo
        UNIQUE (MaDonHang, MaCombo)
);
GO


-------------------------- 15. THANH TOAN ------------------

CREATE TABLE ThanhToan
(
    MaThanhToan BIGINT IDENTITY(1,1) PRIMARY KEY,

    MaDonHang BIGINT NOT NULL UNIQUE,

    PhuongThucThanhToan VARCHAR(30) NOT NULL,
    SoTien DECIMAL(18,2) NOT NULL,

    MaGiaoDich VARCHAR(150) NULL,

    ThoiGianThanhToan DATETIME2 NOT NULL
        DEFAULT SYSDATETIME(),

    TrangThai VARCHAR(30) NOT NULL,

    CONSTRAINT FK_ThanhToan_DonHang
        FOREIGN KEY (MaDonHang)
        REFERENCES DonHang(MaDonHang),

    CONSTRAINT CK_ThanhToan_PhuongThuc
        CHECK (PhuongThucThanhToan IN
        ('CASH',
         'CARD',
         'VNPAY',
         'VIETQR')),

    CONSTRAINT CK_ThanhToan_SoTien
        CHECK (SoTien >= 0),

    CONSTRAINT CK_ThanhToan_TrangThai
        CHECK (TrangThai IN
        ('CHO_THANH_TOAN',
         'THANH_CONG',
         'THAT_BAI',
         'HOAN_TIEN'))
);
GO


-------------------------- 16. NGUYEN LIEU ------------------

CREATE TABLE NguyenLieu
(
    MaNguyenLieu INT IDENTITY(1,1) PRIMARY KEY,
    MaCode VARCHAR(30) NOT NULL UNIQUE,
    TenNguyenLieu NVARCHAR(150) NOT NULL,
    LoaiNguyenLieu NVARCHAR(50) NOT NULL,
    DonViTinh NVARCHAR(20) NOT NULL,
    GiaNhap DECIMAL(18,2) NOT NULL DEFAULT 0,
    TonKhoHienTai DECIMAL(18,3) NOT NULL DEFAULT 0,
    NguongCanhBao DECIMAL(18,3) NOT NULL DEFAULT 0,
    DiUng NVARCHAR(150) NULL,
    TrangThai BIT NOT NULL DEFAULT 1,
    NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    NgayCapNhat DATETIME2 NULL,

    CONSTRAINT CK_NguyenLieu_LoaiNguyenLieu
        CHECK (LoaiNguyenLieu IN
        (N'Thịt',
         N'Rau củ',
         N'Trái cây',
         N'Đồ khô',
         N'Gia vị',
         N'Sốt',
         N'Sữa & phô mai',
         N'Đồ uống',
         N'Đóng gói')),

    CONSTRAINT CK_NguyenLieu_Gia
        CHECK (GiaNhap >= 0 AND TonKhoHienTai >= 0 AND NguongCanhBao >= 0)
);
GO


-------------------------- 17. MON AN - NGUYEN LIEU (CONG THUC) ------------------

CREATE TABLE MonAn_NguyenLieu
(
    MaMonAn INT NOT NULL,
    MaNguyenLieu INT NOT NULL,
    SoLuongCan DECIMAL(18,3) NOT NULL,
    LaTuyChon BIT NOT NULL DEFAULT 0,
    GhiChu NVARCHAR(255) NULL,

    CONSTRAINT PK_MonAn_NguyenLieu
        PRIMARY KEY (MaMonAn, MaNguyenLieu),

    CONSTRAINT FK_MonAnNguyenLieu_MonAn
        FOREIGN KEY (MaMonAn)
        REFERENCES MonAn(MaMonAn),

    CONSTRAINT FK_MonAnNguyenLieu_NguyenLieu
        FOREIGN KEY (MaNguyenLieu)
        REFERENCES NguyenLieu(MaNguyenLieu),

    CONSTRAINT CK_MonAnNguyenLieu_SoLuong
        CHECK (SoLuongCan > 0)
);
GO


-------------------------- VIEW ------------------
CREATE VIEW vw_FPGrowth_Transaction_MonAn
AS
SELECT DISTINCT
    dh.MaDonHang AS TransactionID,
    ctdh.MaMonAn AS ItemID,
    ma.TenMon AS ItemName
FROM DonHang dh, ChiTietDonHang ctdh, MonAn ma
WHERE dh.MaDonHang = ctdh.MaDonHang
  AND ctdh.MaMonAn = ma.MaMonAn
  AND dh.TrangThaiDon <> 'DA_HUY';
GO

CREATE VIEW vw_FPGrowth_Transaction_Combo
AS
SELECT DISTINCT
    dh.MaDonHang AS TransactionID,
    ctdhc.MaCombo AS ItemID,
    cb.TenCombo AS ItemName
FROM DonHang dh, ChiTietDonHangCombo ctdhc, Combo cb
WHERE dh.MaDonHang = ctdhc.MaDonHang
  AND ctdhc.MaCombo = cb.MaCombo
  AND dh.TrangThaiDon <> 'DA_HUY';
GO

CREATE VIEW vw_FPGrowth_Transaction_NguyenLieu
AS
SELECT DISTINCT
    dh.MaDonHang AS TransactionID,
    nl.MaNguyenLieu AS ItemID,
    nl.TenNguyenLieu AS ItemName
FROM DonHang dh, ChiTietDonHang ctdh, MonAn_NguyenLieu mnl, NguyenLieu nl
WHERE dh.MaDonHang    = ctdh.MaDonHang
  AND ctdh.MaMonAn    = mnl.MaMonAn
  AND mnl.MaNguyenLieu= nl.MaNguyenLieu
  AND mnl.LaTuyChon   = 0
  AND dh.TrangThaiDon <> 'DA_HUY';
GO

CREATE VIEW vw_CongThucMonAn
AS
SELECT
    ma.MaMonAn,
    ma.TenMon,
    dm.TenDanhMuc,
    nl.MaNguyenLieu,
    nl.TenNguyenLieu,
    nl.LoaiNguyenLieu,
    mnl.SoLuongCan,
    nl.DonViTinh,
    mnl.LaTuyChon,
    (mnl.SoLuongCan * nl.GiaNhap) AS ChiPhiNguyenLieu
FROM MonAn ma
JOIN DanhMucMonAn dm       ON dm.MaDanhMuc     = ma.MaDanhMuc
JOIN MonAn_NguyenLieu mnl  ON mnl.MaMonAn      = ma.MaMonAn
JOIN NguyenLieu nl         ON nl.MaNguyenLieu  = mnl.MaNguyenLieu;
GO

-------------------------- INDEX ------------------
CREATE INDEX IX_DonHang_KhachHang
ON DonHang(MaKhachHang);
GO

CREATE INDEX IX_DonHang_ThoiGianDat
ON DonHang(ThoiGianDat);
GO

CREATE INDEX IX_DonHang_TrangThai
ON DonHang(TrangThaiDon);
GO

CREATE INDEX IX_DonHang_NguonDon
ON DonHang(NguonDon);
GO

CREATE INDEX IX_ChiTietDonHang_DonHang
ON ChiTietDonHang(MaDonHang);
GO

CREATE INDEX IX_ChiTietDonHang_MonAn
ON ChiTietDonHang(MaMonAn);
GO

CREATE INDEX IX_ChiTietDonHangCombo_DonHang
ON ChiTietDonHangCombo(MaDonHang);
GO

CREATE INDEX IX_ChiTietDonHangCombo_Combo
ON ChiTietDonHangCombo(MaCombo);
GO

CREATE INDEX IX_ThanhToan_ThoiGian
ON ThanhToan(ThoiGianThanhToan);
GO

CREATE INDEX IX_MonAnNguyenLieu_MonAn
ON MonAn_NguyenLieu(MaMonAn);
GO

CREATE INDEX IX_MonAnNguyenLieu_NguyenLieu
ON MonAn_NguyenLieu(MaNguyenLieu);
GO

CREATE INDEX IX_NguyenLieu_Loai
ON NguyenLieu(LoaiNguyenLieu);
GO


/* =====================================================================
   =====================  MASTER DATA (INSERT)  ========================
   Dữ liệu tĩnh cho: vai trò, tài khoản, danh mục, món ăn, nguyên liệu,
   công thức, combo, thuộc tính, khuyến mãi.
   Dữ liệu giao dịch (đơn hàng, thanh toán) được sinh riêng bằng
   data-gen/run.html cho phù hợp khai phá FP-Growth.
   ===================================================================== */

-------------------------- 1. VAI TRO ------------------
SET IDENTITY_INSERT VaiTro ON;
INSERT INTO VaiTro (MaVaiTro, TenVaiTro, MoTa) VALUES
    (1, N'ADMIN',   N'Quản trị viên hệ thống'),
    (2, N'CASHIER', N'Nhân viên thu ngân tại quầy'),
    (3, N'KITCHEN', N'Nhân viên bếp'),
    (4, N'MANAGER', N'Quản lý cửa hàng');
SET IDENTITY_INSERT VaiTro OFF;
GO


-------------------------- 2. TAI KHOAN ------------------
SET IDENTITY_INSERT TaiKhoan ON;
INSERT INTO TaiKhoan (MaTaiKhoan, MaVaiTro, TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai) VALUES
    (1, 1, 'admin',   '$2a$10$HASHADMIN000000000000000000000000000000',   N'Quản trị viên',    'admin@fastpos.vn',   '0900000001'),
    (2, 4, 'manager', '$2a$10$HASHMANAGER0000000000000000000000000000',   N'Trần Thị Quản Lý', 'manager@fastpos.vn', '0900000002'),
    (3, 2, 'nv001',   '$2a$10$HASHNV0010000000000000000000000000000000', N'Nguyễn Quốc Huy',  'huy.nv@fastpos.vn',  '0908111001'),
    (4, 2, 'nv002',   '$2a$10$HASHNV0020000000000000000000000000000000', N'Hoàng Văn Hoan',   'hoan.nv@fastpos.vn', '0908111002'),
    (5, 2, 'nv003',   '$2a$10$HASHNV0030000000000000000000000000000000', N'Phạm Quốc Vũ',     'vu.nv@fastpos.vn',   '0908111003'),
    (6, 3, 'bep001',  '$2a$10$HASHBEP0010000000000000000000000000000000',N'Lê Văn Bếp',       'bep1@fastpos.vn',    '0908222001'),
    (7, 3, 'bep002',  '$2a$10$HASHBEP0020000000000000000000000000000000',N'Trần Văn Nướng',   'bep2@fastpos.vn',    '0908222002');
SET IDENTITY_INSERT TaiKhoan OFF;
GO


-------------------------- 5. DANH MUC MON AN ------------------
SET IDENTITY_INSERT DanhMucMonAn ON;
INSERT INTO DanhMucMonAn (MaDanhMuc, TenDanhMuc, MoTa) VALUES
    (1, N'Burger',      N'Các loại burger thịt bò, gà, cá'),
    (2, N'Gà rán',      N'Gà rán truyền thống, gà cay, cánh gà'),
    (3, N'Pizza',       N'Pizza các loại size vừa'),
    (4, N'Mì Ý',        N'Mì Ý sốt bò, sốt kem'),
    (5, N'Ăn kèm',      N'Khoai tây chiên, salad, vòng hành'),
    (6, N'Nước uống',   N'Nước có gas, trà, cà phê'),
    (7, N'Tráng miệng', N'Kem, bánh, chè');
SET IDENTITY_INSERT DanhMucMonAn OFF;
GO


-------------------------- 6. MON AN (32 mon) ------------------
SET IDENTITY_INSERT MonAn ON;
INSERT INTO MonAn (MaMonAn, MaDanhMuc, TenMon, MoTa, GiaBan) VALUES
    -- Burger (1-6)
    (1,  1, N'Burger Bò Phô Mai',       N'Thịt bò Úc + phô mai Cheddar + sốt BBQ',      65000),
    (2,  1, N'Burger Bò Đôi',           N'2 miếng thịt bò + phô mai + bacon',            89000),
    (3,  1, N'Burger Gà Giòn',          N'Ức gà tẩm bột chiên giòn + xà lách + mayo',   55000),
    (4,  1, N'Burger Gà Cay',           N'Gà giòn vị cay Hàn Quốc',                     55000),
    (5,  1, N'Burger Cá Fillet',        N'Cá tuyết fillet chiên + sốt tartar',          58000),
    (6,  1, N'Burger Tôm',              N'Tôm sú chiên xù + sốt mayo',                  60000),
    -- Gà rán (7-11)
    (7,  2, N'Gà Rán Truyền Thống 1m',  N'Đùi gà rán vàng ươm, giòn tan',               39000),
    (8,  2, N'Gà Rán Cay 1 miếng',      N'Đùi gà rán cay giòn Hàn Quốc',                39000),
    (9,  2, N'Cánh Gà Sốt Cay (4c)',    N'4 cánh gà sốt cay đặc biệt',                  79000),
    (10, 2, N'Gà Popcorn',              N'Miếng gà nhỏ chiên xù, giòn xốp',             49000),
    (11, 2, N'Gà Không Xương',          N'Ức gà chiên tẩm bột, không xương',            69000),
    -- Pizza (12-15)
    (12, 3, N'Pizza Hải Sản (M)',       N'Tôm + mực + phô mai Mozzarella',              149000),
    (13, 3, N'Pizza Bò BBQ (M)',        N'Bò xay + hành tây + sốt BBQ',                 139000),
    (14, 3, N'Pizza Phô Mai 4 Loại',    N'Mozzarella + Cheddar + Parmesan + Gouda',     159000),
    (15, 3, N'Pizza Gà Nấm',            N'Gà + nấm mỡ + phô mai',                       135000),
    -- Mì Ý (16-18)
    (16, 4, N'Mì Ý Sốt Bò Bằm',         N'Spaghetti + sốt bolognese',                   79000),
    (17, 4, N'Mì Ý Sốt Kem Gà',         N'Spaghetti + kem tươi + gà + nấm',             75000),
    (18, 4, N'Mì Ý Hải Sản',            N'Spaghetti + tôm + mực',                       89000),
    -- Ăn kèm (19-23)
    (19, 5, N'Khoai Tây Chiên (M)',     N'Khoai tây chiên size vừa',                    29000),
    (20, 5, N'Khoai Tây Chiên (L)',     N'Khoai tây chiên size lớn cho 2 người',        39000),
    (21, 5, N'Vòng Hành Chiên',         N'Vòng hành tây chiên xù giòn',                 35000),
    (22, 5, N'Salad Trộn',              N'Rau xà lách trộn sốt mayonnaise',             32000),
    (23, 5, N'Bắp Cải Trộn',            N'Bắp cải tím trộn sốt mayo',                   25000),
    -- Nước uống (24-29)
    (24, 6, N'Coca-Cola (M)',           N'Nước ngọt có gas size vừa 350ml',             22000),
    (25, 6, N'Coca-Cola (L)',           N'Nước ngọt có gas size lớn 500ml',             29000),
    (26, 6, N'Pepsi (M)',               N'Nước ngọt có gas size vừa 350ml',             22000),
    (27, 6, N'Pepsi (L)',               N'Nước ngọt có gas size lớn 500ml',             29000),
    (28, 6, N'Trà Đào Cam Sả',          N'Trà đào ướp lạnh với cam và sả',              35000),
    (29, 6, N'Cà Phê Sữa Đá',           N'Cà phê rang xay pha với sữa đặc',             25000),
    -- Tráng miệng (30-32)
    (30, 7, N'Kem Ốc Quế Vani',         N'Kem tươi vị vani + ốc quế giòn',              15000),
    (31, 7, N'Sundae Chocolate',        N'Kem tươi + sốt chocolate',                    25000),
    (32, 7, N'Bánh Táo Nướng',          N'Bánh táo ấm nóng nhân táo Mỹ',                19000);
SET IDENTITY_INSERT MonAn OFF;
GO


-------------------------- 7. THUOC TINH ------------------
SET IDENTITY_INSERT ThuocTinh ON;
INSERT INTO ThuocTinh (MaThuocTinh, TenThuocTinh, MoTa) VALUES
    (1, N'SIZE',     N'Kích cỡ món ăn (S/M/L)'),
    (2, N'CAY',      N'Mức độ cay của món'),
    (3, N'CALORIES', N'Lượng calo ước tính'),
    (4, N'ALLERGEN', N'Cảnh báo dị ứng');
SET IDENTITY_INSERT ThuocTinh OFF;
GO


-------------------------- 8. MON AN - THUOC TINH ------------------
INSERT INTO MonAn_ThuocTinh (MaMonAn, MaThuocTinh, GiaTri) VALUES
    -- SIZE
    (1,  1, N'M'), (2,  1, N'L'), (3,  1, N'M'), (4,  1, N'M'), (5,  1, N'M'), (6,  1, N'M'),
    (12, 1, N'M'), (13, 1, N'M'), (14, 1, N'M'), (15, 1, N'M'),
    (19, 1, N'M'), (20, 1, N'L'),
    (24, 1, N'M'), (25, 1, N'L'), (26, 1, N'M'), (27, 1, N'L'),
    -- CAY
    (4, 2, N'Cay vừa'), (8, 2, N'Cay vừa'), (9, 2, N'Cay vừa'),
    -- ALLERGEN
    (5,  4, N'Hải sản'), (6,  4, N'Hải sản'),
    (12, 4, N'Hải sản'), (18, 4, N'Hải sản');
GO


-------------------------- 9. COMBO (8 combo) ------------------
SET IDENTITY_INSERT Combo ON;
INSERT INTO Combo (MaCombo, TenCombo, MoTa, GiaBan) VALUES
    (1, N'Combo Burger Bò Cổ Điển',     N'Burger Bò Phô Mai + Khoai M + Coca M',            99000),
    (2, N'Combo Gà Rán 2 Miếng',        N'2 Gà Rán TT + Khoai M + Pepsi M',                 89000),
    (3, N'Combo Bữa Trưa Tiết Kiệm',    N'Burger Gà + Khoai M + Coca M',                    75000),
    (4, N'Combo Gia Đình 4 Người',      N'4 Burger + 4 Khoai + 4 Nước L + 2 Kem',          349000),
    (5, N'Combo Pizza Đôi',             N'2 Pizza M + 2 Coca L',                           259000),
    (6, N'Combo Cánh Gà Party',         N'2 phần Cánh Gà + Khoai L + 2 Pepsi L',           199000),
    (7, N'Combo Mì Ý Đôi',              N'2 Mì Ý + 2 Nước + Salad',                        179000),
    (8, N'Combo Sáng Cà Phê',           N'Cà Phê Sữa Đá + Bánh Táo Nướng',                  45000);
SET IDENTITY_INSERT Combo OFF;
GO


-------------------------- 10. CHI TIET COMBO ------------------
INSERT INTO ChiTietCombo (MaCombo, MaMonAn, SoLuong) VALUES
    -- Combo 1: Burger Bò + Khoai M + Coca M
    (1, 1,  1), (1, 19, 1), (1, 24, 1),
    -- Combo 2: 2 Gà Rán TT + Khoai M + Pepsi M
    (2, 7,  2), (2, 19, 1), (2, 26, 1),
    -- Combo 3: Burger Gà + Khoai M + Coca M
    (3, 3,  1), (3, 19, 1), (3, 24, 1),
    -- Combo 4: Gia đình
    (4, 1,  2), (4, 3,  2), (4, 20, 2), (4, 25, 4), (4, 30, 2),
    -- Combo 5: Pizza đôi
    (5, 12, 1), (5, 13, 1), (5, 25, 2),
    -- Combo 6: Cánh gà party
    (6, 9,  2), (6, 20, 1), (6, 27, 2),
    -- Combo 7: Mì Ý đôi
    (7, 16, 1), (7, 17, 1), (7, 24, 2), (7, 22, 1),
    -- Combo 8: Sáng cà phê
    (8, 29, 1), (8, 32, 1);
GO


-------------------------- 11. KHUYEN MAI ------------------
SET IDENTITY_INSERT KhuyenMai ON;
INSERT INTO KhuyenMai (MaKhuyenMai, MaCode, TenKhuyenMai, MoTa, LoaiGiam, GiaTriGiam, GiaTriDonToiThieu, GiamToiDa, NgayBatDau, NgayKetThuc) VALUES
    (1, 'HUIT10',   N'Giảm 10% sinh viên HUIT',  N'Áp dụng cho đơn từ 100k',  N'PHAN_TRAM', 10,    100000, 50000, '2026-08-01', '2026-12-31'),
    (2, 'LUNCH20',  N'Giảm 20% bữa trưa',        N'Áp dụng khung 11h-13h',    N'PHAN_TRAM', 20,    150000, 80000, '2026-08-01', '2026-12-31'),
    (3, 'FREESHIP', N'Giảm cứng 15.000đ',        N'Đơn từ 50k',               N'SO_TIEN',   15000, 50000,  NULL,  '2026-08-01', '2026-12-31'),
    (4, 'NEWCUST',  N'Khách mới giảm 30k',       N'Đơn từ 200k',              N'SO_TIEN',   30000, 200000, NULL,  '2026-08-01', '2026-12-31');
SET IDENTITY_INSERT KhuyenMai OFF;
GO


-------------------------- 16. NGUYEN LIEU (40 nguyen lieu) ------------------
SET IDENTITY_INSERT NguyenLieu ON;
INSERT INTO NguyenLieu (MaNguyenLieu, MaCode, TenNguyenLieu, LoaiNguyenLieu, DonViTinh, GiaNhap, TonKhoHienTai, NguongCanhBao, DiUng) VALUES
    -- Thịt & hải sản (1-8)
    (1,  'NL_THITBOUC',     N'Thịt bò Úc xay',            N'Thịt',            N'g',   220,   8000,   1500, NULL),
    (2,  'NL_UCGA',         N'Ức gà phi lê',              N'Thịt',            N'g',   80,   12000,   2000, NULL),
    (3,  'NL_DUIGA',        N'Đùi gà tươi',               N'Thịt',            N'g',   90,   15000,   2500, NULL),
    (4,  'NL_CANHGA',       N'Cánh gà',                   N'Thịt',            N'g',   85,    8000,   1500, NULL),
    (5,  'NL_TOMSU',        N'Tôm sú bóc vỏ',             N'Thịt',            N'g',   350,   3000,    500, N'Hải sản'),
    (6,  'NL_CATUYET',      N'Cá tuyết fillet',           N'Thịt',            N'g',   280,   4000,    600, NULL),
    (7,  'NL_MUC',          N'Mực ống làm sạch',          N'Thịt',            N'g',   260,   2500,    500, N'Hải sản'),
    (8,  'NL_BACON',        N'Thịt xông khói (bacon)',    N'Thịt',            N'g',   180,   3000,    600, NULL),
    -- Sữa & phô mai (9-12)
    (9,  'NL_PHOMAI_CHE',   N'Phô mai Cheddar (lát)',     N'Sữa & phô mai',   N'lát', 2500,  1500,    300, N'Sữa'),
    (10, 'NL_PHOMAI_MOZ',   N'Phô mai Mozzarella',        N'Sữa & phô mai',   N'g',   400,   5000,    800, N'Sữa'),
    (11, 'NL_BO',           N'Bơ nhạt',                   N'Sữa & phô mai',   N'g',   200,   2500,    400, N'Sữa'),
    (12, 'NL_KEM_TUOI',     N'Kem tươi vani',             N'Sữa & phô mai',   N'ml',  150,   6000,   1000, N'Sữa'),
    -- Rau củ (13-19)
    (13, 'NL_KHOAITAY',     N'Khoai tây tươi',            N'Rau củ',          N'g',   35,   20000,   3000, NULL),
    (14, 'NL_XALACH',       N'Xà lách mỡ',                N'Rau củ',          N'g',   40,    4000,    600, NULL),
    (15, 'NL_CACHUA',       N'Cà chua bi',                N'Rau củ',          N'g',   45,    5000,    800, NULL),
    (16, 'NL_HANHTAY',      N'Hành tây',                  N'Rau củ',          N'g',   25,    6000,    800, NULL),
    (17, 'NL_BAPCAI',       N'Bắp cải tím',               N'Rau củ',          N'g',   30,    3500,    500, NULL),
    (18, 'NL_DUACHUOT',     N'Dưa chuột (dưa leo)',       N'Rau củ',          N'g',   35,    3000,    500, NULL),
    (19, 'NL_NAM',          N'Nấm mỡ',                    N'Rau củ',          N'g',   120,   2000,    400, NULL),
    -- Trái cây (20-22)
    (20, 'NL_TAO',          N'Táo đỏ',                    N'Trái cây',        N'g',   65,    3000,    500, NULL),
    (21, 'NL_CAM',          N'Cam sành',                  N'Trái cây',        N'g',   55,    5000,    800, NULL),
    (22, 'NL_DAO',          N'Đào ngâm',                  N'Trái cây',        N'g',   120,   2000,    400, NULL),
    -- Đồ khô (23-27)
    (23, 'NL_BOTMI',        N'Bột mì đa dụng',            N'Đồ khô',          N'g',    20,  30000,   5000, N'Gluten'),
    (24, 'NL_BANHMI_BUR',   N'Bánh mì burger (ổ)',        N'Đồ khô',          N'cái', 5000,  2000,    300, N'Gluten'),
    (25, 'NL_DEPIZZA',      N'Đế pizza (size M)',         N'Đồ khô',          N'cái',18000,   300,     50, N'Gluten'),
    (26, 'NL_MISOI',        N'Mì Ý spaghetti (khô)',      N'Đồ khô',          N'g',    60,   8000,   1200, N'Gluten'),
    (27, 'NL_BOTCHIENGION', N'Bột chiên giòn',            N'Đồ khô',          N'g',    35,   6000,   1000, N'Gluten'),
    -- Gia vị (28-30)
    (28, 'NL_MUOI',         N'Muối tinh',                 N'Gia vị',          N'g',     8,   5000,   1000, NULL),
    (29, 'NL_TIEU',         N'Tiêu đen xay',              N'Gia vị',          N'g',   400,   1000,    200, NULL),
    (30, 'NL_OTBOT',        N'Ớt bột Hàn Quốc',           N'Gia vị',          N'g',   280,   1500,    300, NULL),
    -- Sốt (31-33)
    (31, 'NL_SOTBBQ',       N'Sốt BBQ',                   N'Sốt',             N'ml',  180,   3000,    500, NULL),
    (32, 'NL_SOTMAYO',      N'Sốt mayonnaise',            N'Sốt',             N'ml',  160,   4000,    600, N'Trứng'),
    (33, 'NL_SOTTARTAR',    N'Sốt tartar',                N'Sốt',             N'ml',  200,   2000,    400, N'Trứng'),
    -- Đồ uống (34-39)
    (34, 'NL_COCA',         N'Coca-Cola concentrate',     N'Đồ uống',         N'ml',   15,  30000,   5000, NULL),
    (35, 'NL_PEPSI',        N'Pepsi concentrate',         N'Đồ uống',         N'ml',   14,  30000,   5000, NULL),
    (36, 'NL_CAPHE',        N'Cà phê rang xay',           N'Đồ uống',         N'g',   600,   2000,    400, NULL),
    (37, 'NL_SUADAC',       N'Sữa đặc',                   N'Đồ uống',         N'ml',   80,   3000,    500, N'Sữa'),
    (38, 'NL_TRADAO',       N'Trà đào lá',                N'Đồ uống',         N'g',   450,   1000,    200, NULL),
    (39, 'NL_DA',           N'Đá viên',                   N'Đồ uống',         N'g',     2,  50000,  10000, NULL),
    -- Đóng gói (40)
    (40, 'NL_LY_GIAY',      N'Ly giấy có nắp',            N'Đóng gói',        N'cái',1500,   5000,    800, NULL);
SET IDENTITY_INSERT NguyenLieu OFF;
GO


-------------------------- 17. MON AN - NGUYEN LIEU (CONG THUC) ------------------
INSERT INTO MonAn_NguyenLieu (MaMonAn, MaNguyenLieu, SoLuongCan, LaTuyChon) VALUES
    -- === BURGER ===
    -- 1. Burger Bò Phô Mai
    (1, 1, 120, 0), (1, 24, 1, 0), (1, 9, 2, 0), (1, 14, 20, 0),
    (1, 15, 30, 0), (1, 16, 15, 1), (1, 31, 20, 0),
    -- 2. Burger Bò Đôi
    (2, 1, 240, 0), (2, 24, 1, 0), (2, 9, 2, 0), (2, 8, 30, 0),
    (2, 14, 20, 0), (2, 31, 25, 0), (2, 32, 15, 0),
    -- 3. Burger Gà Giòn
    (3, 2, 120, 0), (3, 24, 1, 0), (3, 27, 40, 0), (3, 14, 20, 0), (3, 32, 20, 0),
    -- 4. Burger Gà Cay
    (4, 2, 120, 0), (4, 24, 1, 0), (4, 27, 40, 0), (4, 30, 5, 0),
    (4, 14, 20, 0), (4, 32, 20, 0),
    -- 5. Burger Cá Fillet
    (5, 6, 130, 0), (5, 24, 1, 0), (5, 27, 35, 0), (5, 14, 20, 0), (5, 33, 25, 0),
    -- 6. Burger Tôm
    (6, 5, 110, 0), (6, 24, 1, 0), (6, 27, 30, 0), (6, 14, 20, 0), (6, 32, 20, 0),

    -- === GÀ RÁN ===
    -- 7. Gà Rán Truyền Thống
    (7, 3, 180, 0), (7, 27, 60, 0), (7, 28, 3, 0), (7, 29, 2, 0),
    -- 8. Gà Rán Cay
    (8, 3, 180, 0), (8, 27, 60, 0), (8, 30, 8, 0), (8, 28, 3, 0),
    -- 9. Cánh Gà Sốt Cay
    (9, 4, 280, 0), (9, 27, 50, 0), (9, 30, 10, 0), (9, 31, 30, 0),
    -- 10. Gà Popcorn
    (10, 2, 150, 0), (10, 27, 50, 0), (10, 28, 3, 0), (10, 29, 2, 0),
    -- 11. Gà Không Xương
    (11, 2, 200, 0), (11, 27, 55, 0), (11, 31, 30, 1), (11, 28, 3, 0),

    -- === PIZZA ===
    -- 12. Pizza Hải Sản
    (12, 25, 1, 0), (12, 10, 100, 0), (12, 5, 60, 0), (12, 7, 60, 0),
    (12, 15, 40, 0), (12, 16, 20, 0),
    -- 13. Pizza Bò BBQ
    (13, 25, 1, 0), (13, 10, 100, 0), (13, 1, 100, 0), (13, 16, 30, 0), (13, 31, 40, 0),
    -- 14. Pizza Phô Mai 4 Loại
    (14, 25, 1, 0), (14, 10, 180, 0), (14, 9, 3, 0), (14, 11, 20, 0),
    -- 15. Pizza Gà Nấm
    (15, 25, 1, 0), (15, 10, 100, 0), (15, 2, 90, 0), (15, 19, 60, 0),

    -- === MÌ Ý ===
    -- 16. Mì Ý Sốt Bò Bằm
    (16, 26, 120, 0), (16, 1, 100, 0), (16, 15, 60, 0), (16, 16, 25, 0), (16, 10, 20, 1),
    -- 17. Mì Ý Sốt Kem Gà
    (17, 26, 120, 0), (17, 2, 100, 0), (17, 12, 80, 0), (17, 19, 40, 0), (17, 11, 15, 0),
    -- 18. Mì Ý Hải Sản
    (18, 26, 120, 0), (18, 5, 60, 0), (18, 7, 60, 0), (18, 15, 50, 0),

    -- === ĂN KÈM ===
    -- 19. Khoai Tây Chiên (M)
    (19, 13, 150, 0), (19, 28, 2, 0),
    -- 20. Khoai Tây Chiên (L)
    (20, 13, 250, 0), (20, 28, 3, 0),
    -- 21. Vòng Hành Chiên
    (21, 16, 150, 0), (21, 27, 60, 0), (21, 28, 2, 0),
    -- 22. Salad Trộn
    (22, 14, 80, 0), (22, 15, 50, 0), (22, 18, 40, 0), (22, 32, 20, 0),
    -- 23. Bắp Cải Trộn
    (23, 17, 100, 0), (23, 18, 30, 0), (23, 32, 15, 0),

    -- === NƯỚC UỐNG ===
    -- 24. Coca-Cola (M)
    (24, 34, 50, 0), (24, 39, 80, 0), (24, 40, 1, 0),
    -- 25. Coca-Cola (L)
    (25, 34, 80, 0), (25, 39, 120, 0), (25, 40, 1, 0),
    -- 26. Pepsi (M)
    (26, 35, 50, 0), (26, 39, 80, 0), (26, 40, 1, 0),
    -- 27. Pepsi (L)
    (27, 35, 80, 0), (27, 39, 120, 0), (27, 40, 1, 0),
    -- 28. Trà Đào Cam Sả
    (28, 38, 8, 0), (28, 22, 30, 0), (28, 21, 30, 0), (28, 39, 100, 0), (28, 40, 1, 0),
    -- 29. Cà Phê Sữa Đá
    (29, 36, 15, 0), (29, 37, 30, 0), (29, 39, 120, 0), (29, 40, 1, 0),

    -- === TRÁNG MIỆNG ===
    -- 30. Kem Ốc Quế Vani
    (30, 12, 80, 0), (30, 23, 20, 0),
    -- 31. Sundae Chocolate
    (31, 12, 100, 0), (31, 11, 10, 0),
    -- 32. Bánh Táo Nướng
    (32, 20, 80, 0), (32, 23, 60, 0), (32, 11, 15, 0);
GO


-------------------------- KIEM TRA NHANH ------------------
PRINT '=== KIEM TRA DU LIEU DA NAP ===';
SELECT 'VaiTro' AS Bang, COUNT(*) AS SoDong FROM VaiTro
UNION ALL SELECT 'TaiKhoan',          COUNT(*) FROM TaiKhoan
UNION ALL SELECT 'DanhMucMonAn',      COUNT(*) FROM DanhMucMonAn
UNION ALL SELECT 'MonAn',             COUNT(*) FROM MonAn
UNION ALL SELECT 'ThuocTinh',         COUNT(*) FROM ThuocTinh
UNION ALL SELECT 'MonAn_ThuocTinh',   COUNT(*) FROM MonAn_ThuocTinh
UNION ALL SELECT 'Combo',             COUNT(*) FROM Combo
UNION ALL SELECT 'ChiTietCombo',      COUNT(*) FROM ChiTietCombo
UNION ALL SELECT 'KhuyenMai',         COUNT(*) FROM KhuyenMai
UNION ALL SELECT 'NguyenLieu',        COUNT(*) FROM NguyenLieu
UNION ALL SELECT 'MonAn_NguyenLieu',  COUNT(*) FROM MonAn_NguyenLieu;
GO

PRINT '=== TOP 5 MON CO CHI PHI NGUYEN LIEU CAO NHAT ===';
SELECT TOP 5
    TenMon,
    SUM(ChiPhiNguyenLieu) AS TongChiPhiNL,
    COUNT(*) AS SoNguyenLieu
FROM vw_CongThucMonAn
GROUP BY MaMonAn, TenMon
ORDER BY TongChiPhiNL DESC;
GO
