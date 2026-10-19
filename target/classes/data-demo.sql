-------------------------- 1. VAI TRO ------------------

INSERT INTO VaiTro (MaVaiTro, TenVaiTro, MoTa) VALUES
    (1, N'ADMIN',   N'Quản trị viên hệ thống'),
    (2, N'CASHIER', N'Nhân viên thu ngân tại quầy'),
    (3, N'KITCHEN', N'Nhân viên bếp'),
    (4, N'MANAGER', N'Quản lý cửa hàng');



-------------------------- 5. DANH MUC MON AN ------------------

INSERT INTO DanhMucMonAn (MaDanhMuc, TenDanhMuc, MoTa) VALUES
    (1, N'Burger',      N'Các loại burger thịt bò, gà, cá'),
    (2, N'Gà rán',      N'Gà rán truyền thống, gà cay, cánh gà'),
    (3, N'Pizza',       N'Pizza các loại size vừa'),
    (4, N'Mì Ý',        N'Mì Ý sốt bò, sốt kem'),
    (5, N'Ăn kèm',      N'Khoai tây chiên, salad, vòng hành'),
    (6, N'Nước uống',   N'Nước có gas, trà, cà phê'),
    (7, N'Tráng miệng', N'Kem, bánh, chè');



-------------------------- 6. MON AN (32 mon) ------------------

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



-------------------------- 7. THUOC TINH ------------------

INSERT INTO ThuocTinh (MaThuocTinh, TenThuocTinh, MoTa) VALUES
    (1, N'SIZE',     N'Kích cỡ món ăn (S/M/L)'),
    (2, N'CAY',      N'Mức độ cay của món'),
    (3, N'CALORIES', N'Lượng calo ước tính'),
    (4, N'ALLERGEN', N'Cảnh báo dị ứng');



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


-------------------------- 9. COMBO (8 combo) ------------------

INSERT INTO Combo (MaCombo, TenCombo, MoTa, GiaBan) VALUES
    (1, N'Combo Burger Bò Cổ Điển',     N'Burger Bò Phô Mai + Khoai M + Coca M',            99000),
    (2, N'Combo Gà Rán 2 Miếng',        N'2 Gà Rán TT + Khoai M + Pepsi M',                 89000),
    (3, N'Combo Bữa Trưa Tiết Kiệm',    N'Burger Gà + Khoai M + Coca M',                    75000),
    (4, N'Combo Gia Đình 4 Người',      N'4 Burger + 4 Khoai + 4 Nước L + 2 Kem',          349000),
    (5, N'Combo Pizza Đôi',             N'2 Pizza M + 2 Coca L',                           259000),
    (6, N'Combo Cánh Gà Party',         N'2 phần Cánh Gà + Khoai L + 2 Pepsi L',           199000),
    (7, N'Combo Mì Ý Đôi',              N'2 Mì Ý + 2 Nước + Salad',                        179000),
    (8, N'Combo Sáng Cà Phê',           N'Cà Phê Sữa Đá + Bánh Táo Nướng',                  45000);



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


-------------------------- 11. KHUYEN MAI ------------------

INSERT INTO KhuyenMai (MaKhuyenMai, MaCode, TenKhuyenMai, MoTa, LoaiGiam, GiaTriGiam, GiaTriDonToiThieu, GiamToiDa, NgayBatDau, NgayKetThuc) VALUES
    (1, 'HUIT10',   N'Giảm 10% sinh viên HUIT',  N'Áp dụng cho đơn từ 100k',  N'PHAN_TRAM', 10,    100000, 50000, '2026-08-01', '2026-12-31'),
    (2, 'LUNCH20',  N'Giảm 20% bữa trưa',        N'Áp dụng khung 11h-13h',    N'PHAN_TRAM', 20,    150000, 80000, '2026-08-01', '2026-12-31'),
    (3, 'FREESHIP', N'Giảm cứng 15.000đ',        N'Đơn từ 50k',               N'SO_TIEN',   15000, 50000,  NULL,  '2026-08-01', '2026-12-31'),
    (4, 'NEWCUST',  N'Khách mới giảm 30k',       N'Đơn từ 200k',              N'SO_TIEN',   30000, 200000, NULL,  '2026-08-01', '2026-12-31');



-------------------------- 16. NGUYEN LIEU (40 nguyen lieu) ------------------

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
