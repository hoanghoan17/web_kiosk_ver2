USE KLCN;
GO
IF OBJECT_ID('dbo.NhatKyAdmin','U') IS NULL
CREATE TABLE dbo.NhatKyAdmin (
 MaNhatKy BIGINT IDENTITY(1,1) PRIMARY KEY,
 TenDangNhap VARCHAR(100) NOT NULL, HanhDong NVARCHAR(80) NOT NULL,
 DoiTuong NVARCHAR(120) NOT NULL, NoiDung NVARCHAR(1000) NOT NULL,
 ThoiGian DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO
IF COL_LENGTH('dbo.MonAn','ConHang') IS NULL
ALTER TABLE MonAn ADD ConHang BIT NOT NULL CONSTRAINT DF_MonAn_ConHang DEFAULT 1;
GO
IF EXISTS (SELECT 1 FROM sys.check_constraints WHERE name='CK_DonHang_TrangThai' AND parent_object_id=OBJECT_ID('dbo.DonHang'))
ALTER TABLE DonHang DROP CONSTRAINT CK_DonHang_TrangThai;
ALTER TABLE DonHang ADD CONSTRAINT CK_DonHang_TrangThai CHECK (TrangThaiDon IN
('CHO_THANH_TOAN','DA_THANH_TOAN','CHO_CHE_BIEN','DANG_CHE_BIEN','HOAN_THANH','DA_GIAO','DA_HUY'));
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_NhatKyAdmin_ThoiGian' AND object_id=OBJECT_ID('dbo.NhatKyAdmin'))
CREATE INDEX IX_NhatKyAdmin_ThoiGian ON NhatKyAdmin(ThoiGian);
GO
-- Run after 01-KLCN.sql on a NEW database, or directly on existing KLCN.
-- Does not drop data; backup before migrating a database already in use.
