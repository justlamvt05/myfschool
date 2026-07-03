-- ============================================================
-- PostgreSQL Seed Data Script
-- 1 Student + Lịch học + Bảng điểm
-- Database: myfschool
-- ============================================================

-- 1. Fix check constraint & insert Role
ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check;
ALTER TABLE roles ADD CONSTRAINT roles_name_check
    CHECK (name::text = ANY (ARRAY['ROLE_ADMIN', 'ROLE_PARENT', 'ROLE_TEACHER', 'ROLE_STUDENT']));

INSERT INTO roles (id, name, created_at, updated_at)
VALUES
    (1, 'ROLE_STUDENT', NOW(), NOW())
   
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name, created_at, updated_at)
VALUES  (2, 'ROLE_ADMIN',   NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 2. User (password = BCrypt encode of "123456")
INSERT INTO tbl_users (id, username, email, password, full_name, phone, status, created_at, updated_at)
VALUES (
    1,
    'nguyenvana',
    'nguyenvana@gmail.com',
    '$2a$10$KU1x/wB.Z9Amz3RDUW/v0eSCl2i2N4ILVUnOHV8WJUgBsKpJsIUD2',
    'Nguyễn Văn A',
    '0901234567',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 3. User - Role
INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;

-- 4. ClassRoom
INSERT INTO class_rooms (id, class_name, school_year, created_at, updated_at)
VALUES (1, '12A1', '2025-2026', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 5. Student
INSERT INTO students (id, student_code, date_of_birth, gender, address, user_id, class_room_id, created_at, updated_at)
VALUES (
    1,
    'HS2025001',
    '2007-05-15',
    'Nam',
    '123 Nguyễn Huệ, Quận 1, TP.HCM',
    1,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 6. Subjects (11 môn)
INSERT INTO subjects (id, subject_code, subject_name, created_at, updated_at) VALUES
    (1,  'TOAN',    'Toán',           NOW(), NOW()),
    (2,  'VAN',     'Ngữ Văn',        NOW(), NOW()),
    (3,  'ANH',     'Tiếng Anh',      NOW(), NOW()),
    (4,  'LY',      'Vật Lý',         NOW(), NOW()),
    (5,  'HOA',     'Hóa Học',        NOW(), NOW()),
    (6,  'SINH',    'Sinh Học',       NOW(), NOW()),
    (7,  'SU',      'Lịch Sử',       NOW(), NOW()),
    (8,  'DIA',     'Địa Lý',        NOW(), NOW()),
    (9,  'GDCD',    'Công Dân',       NOW(), NOW()),
    (10, 'TDUC',    'Thể Dục',       NOW(), NOW()),
    (11, 'GDQP',    'Giáo Dục Quốc Phòng', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 7. Semesters
INSERT INTO semesters (id, semester_name, school_year, created_at, updated_at) VALUES
    (1, 'Học kỳ 1', '2025-2026', NOW(), NOW()),
    (2, 'Học kỳ 2', '2025-2026', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 8. Schedules – Lịch học lớp 12A1 (11 môn)
--    day_of_week: 0=MONDAY, 1=TUESDAY, ... 5=SATURDAY
-- ============================================================
INSERT INTO schedules (id, class_room_id, subject_id, day_of_week, period_start, period_end, room, created_at, updated_at) VALUES
    -- Thứ 2 (MONDAY = 0) - 5 tiết
    (1,  1, 1,  0, 1, 2, 'P.201',  NOW(), NOW()),   -- Toán tiết 1-2
    (2,  1, 2,  0, 3, 4, 'P.202',  NOW(), NOW()),   -- Ngữ Văn tiết 3-4
    (3,  1, 3,  0, 5, 5, 'P.301',  NOW(), NOW()),   -- Tiếng Anh tiết 5

    -- Thứ 3 (TUESDAY = 1) - 5 tiết
    (4,  1, 4,  1, 1, 2, 'P.Lab1', NOW(), NOW()),   -- Vật Lý tiết 1-2
    (5,  1, 5,  1, 3, 4, 'P.Lab2', NOW(), NOW()),   -- Hóa Học tiết 3-4
    (6,  1, 6,  1, 5, 5, 'P.Lab3', NOW(), NOW()),   -- Sinh Học tiết 5

    -- Thứ 4 (WEDNESDAY = 2) - 5 tiết
    (7,  1, 7,  2, 1, 2, 'P.203',  NOW(), NOW()),   -- Lịch Sử tiết 1-2
    (8,  1, 8,  2, 3, 4, 'P.204',  NOW(), NOW()),   -- Địa Lý tiết 3-4
    (9,  1, 9,  2, 5, 5, 'P.205',  NOW(), NOW()),   -- Công Dân tiết 5

    -- Thứ 5 (THURSDAY = 3) - 5 tiết
    (10, 1, 1,  3, 1, 2, 'P.201',  NOW(), NOW()),   -- Toán tiết 1-2
    (11, 1, 3,  3, 3, 4, 'P.301',  NOW(), NOW()),   -- Tiếng Anh tiết 3-4
    (12, 1, 2,  3, 5, 5, 'P.202',  NOW(), NOW()),   -- Ngữ Văn tiết 5

    -- Thứ 6 (FRIDAY = 4) - 5 tiết
    (13, 1, 5,  4, 1, 2, 'P.Lab2', NOW(), NOW()),   -- Hóa Học tiết 1-2
    (14, 1, 4,  4, 3, 4, 'P.Lab1', NOW(), NOW()),   -- Vật Lý tiết 3-4
    (15, 1, 6,  4, 5, 5, 'P.Lab3', NOW(), NOW()),   -- Sinh Học tiết 5

    -- Thứ 7 (SATURDAY = 5) - 5 tiết
    (16, 1, 10, 5, 1, 2, 'SVĐ',    NOW(), NOW()),   -- Thể Dục tiết 1-2
    (17, 1, 11, 5, 3, 4, 'P.GDQP', NOW(), NOW()),   -- GDQP tiết 3-4
    (18, 1, 9,  5, 5, 5, 'P.205',  NOW(), NOW())    -- Công Dân tiết 5
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 9. Grades – Bảng điểm học sinh Nguyễn Văn A
--    Học kỳ 1 + Học kỳ 2, mỗi kỳ 6 môn chính
-- ============================================================
INSERT INTO grades (id, student_id, subject_id, semester_id, oral_score, score15_min, score1_period, final_exam, average_score, created_at, updated_at) VALUES
    -- ===== HỌC KỲ 1 =====
    (1,  1, 1, 1, 8.0, 7.5, 8.0, 8.5, 8.1,  NOW(), NOW()),  -- Toán
    (2,  1, 2, 1, 7.0, 7.0, 7.5, 7.0, 7.1,  NOW(), NOW()),  -- Ngữ Văn
    (3,  1, 3, 1, 9.0, 8.5, 9.0, 9.0, 8.9,  NOW(), NOW()),  -- Tiếng Anh
    (4,  1, 4, 1, 6.5, 7.0, 6.5, 7.0, 6.8,  NOW(), NOW()),  -- Vật Lý
    (5,  1, 5, 1, 7.5, 8.0, 7.0, 7.5, 7.5,  NOW(), NOW()),  -- Hóa Học
    (6,  1, 6, 1, 8.0, 7.5, 8.5, 8.0, 8.0,  NOW(), NOW()),  -- Sinh Học

    -- ===== HỌC KỲ 2 =====
    (7,  1, 1, 2, 8.5, 8.0, 8.5, 9.0, 8.6,  NOW(), NOW()),  -- Toán
    (8,  1, 2, 2, 7.5, 7.5, 8.0, 7.5, 7.6,  NOW(), NOW()),  -- Ngữ Văn
    (9,  1, 3, 2, 9.5, 9.0, 9.5, 9.5, 9.4,  NOW(), NOW()),  -- Tiếng Anh
    (10, 1, 4, 2, 7.0, 7.5, 7.0, 7.5, 7.3,  NOW(), NOW()),  -- Vật Lý
    (11, 1, 5, 2, 8.0, 8.5, 7.5, 8.0, 8.0,  NOW(), NOW()),  -- Hóa Học
    (12, 1, 6, 2, 8.5, 8.0, 9.0, 8.5, 8.5,  NOW(), NOW())   -- Sinh Học
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 10. Student 2 – Trần Thị B (cùng lớp 12A1)
-- ============================================================

-- User 2
INSERT INTO tbl_users (id, username, email, password, full_name, phone, status, created_at, updated_at)
VALUES (
    2,
    'tranthib',
    'tranthib@gmail.com',
    '$2a$10$KU1x/wB.Z9Amz3RDUW/v0eSCl2i2N4ILVUnOHV8WJUgBsKpJsIUD2',
    'Trần Thị B',
    '0912345678',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- User 2 - Role
INSERT INTO user_roles (user_id, role_id)
VALUES (2, 1)
ON CONFLICT DO NOTHING;

-- Student 2
INSERT INTO students (id, student_code, date_of_birth, gender, address, user_id, class_room_id, created_at, updated_at)
VALUES (
    2,
    'HS2025002',
    '2007-09-20',
    'Nữ',
    '456 Lê Lợi, Quận 3, TP.HCM',
    2,
    1,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 5.1. Student Class Rooms (History)
INSERT INTO student_classrooms (id, student_id, class_room_id, join_date, status, created_at, updated_at)
VALUES 
    (1, 1, 1, '2025-08-15', 'CURRENT', NOW(), NOW()),
    (2, 2, 1, '2025-08-15', 'CURRENT', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Grades – Bảng điểm Trần Thị B
INSERT INTO grades (id, student_id, subject_id, semester_id, oral_score, score15_min, score1_period, final_exam, average_score, created_at, updated_at) VALUES
    -- ===== HỌC KỲ 1 =====
    (13, 2, 1, 1, 9.0, 8.5, 9.0, 9.5, 9.1,  NOW(), NOW()),  -- Toán
    (14, 2, 2, 1, 8.5, 8.0, 8.0, 8.5, 8.3,  NOW(), NOW()),  -- Ngữ Văn
    (15, 2, 3, 1, 7.5, 7.0, 7.5, 7.0, 7.2,  NOW(), NOW()),  -- Tiếng Anh
    (16, 2, 4, 1, 8.0, 8.5, 8.0, 8.0, 8.1,  NOW(), NOW()),  -- Vật Lý
    (17, 2, 5, 1, 6.5, 7.0, 7.0, 6.5, 6.7,  NOW(), NOW()),  -- Hóa Học
    (18, 2, 6, 1, 7.0, 7.5, 7.0, 7.5, 7.3,  NOW(), NOW()),  -- Sinh Học

    -- ===== HỌC KỲ 2 =====
    (19, 2, 1, 2, 9.5, 9.0, 9.5, 10.0, 9.6, NOW(), NOW()),  -- Toán
    (20, 2, 2, 2, 8.0, 8.5, 8.5, 8.0, 8.2,  NOW(), NOW()),  -- Ngữ Văn
    (21, 2, 3, 2, 8.0, 7.5, 8.0, 7.5, 7.7,  NOW(), NOW()),  -- Tiếng Anh
    (22, 2, 4, 2, 8.5, 9.0, 8.5, 8.5, 8.6,  NOW(), NOW()),  -- Vật Lý
    (23, 2, 5, 2, 7.0, 7.5, 7.5, 7.0, 7.2,  NOW(), NOW()),  -- Hóa Học
    (24, 2, 6, 2, 7.5, 8.0, 7.5, 8.0, 7.8,  NOW(), NOW())   -- Sinh Học
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 11. Clubs (10 câu lạc bộ)
-- ============================================================
INSERT INTO clubs (id, code, name, description, image_url) VALUES
    (1, 'CLB001', 'Câu lạc bộ Âm nhạc', 'Nơi giao lưu âm nhạc', 'https://example.com/music.jpg'),
    (2, 'CLB002', 'Câu lạc bộ Thể thao', 'Rèn luyện sức khỏe', 'https://example.com/sports.jpg'),
    (3, 'CLB003', 'Câu lạc bộ Cờ vua', 'Phát triển tư duy', 'https://example.com/chess.jpg'),
    (4, 'CLB004', 'Câu lạc bộ Lập trình', 'Code cùng nhau', 'https://example.com/coding.jpg'),
    (5, 'CLB005', 'Câu lạc bộ Tiếng Anh', 'Luyện nói tiếng Anh', 'https://example.com/english.jpg'),
    (6, 'CLB006', 'Câu lạc bộ Nhiếp ảnh', 'Sống ảo chuyên nghiệp', 'https://example.com/photo.jpg'),
    (7, 'CLB007', 'Câu lạc bộ Sách', 'Mọt sách hội tụ', 'https://example.com/book.jpg'),
    (8, 'CLB008', 'Câu lạc bộ Tình nguyện', 'Vì cộng đồng', 'https://example.com/volunteer.jpg'),
    (9, 'CLB009', 'Câu lạc bộ Hội họa', 'Sáng tạo nghệ thuật', 'https://example.com/art.jpg'),
    (10, 'CLB010', 'Câu lạc bộ Khiêu vũ', 'Nhịp điệu sôi động', 'https://example.com/dance.jpg')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 12. Club Students – Trần Thị B (user_id = 2, student_id = 2) tham gia 5 CLB đầu
-- ============================================================
INSERT INTO club_students (club_id, student_id) VALUES
    (1, 2),
    (2, 2),
    (3, 2),
    (4, 2),
    (5, 2)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 13. Roles – thêm ROLE_TEACHER
-- ============================================================
INSERT INTO roles (id, name, created_at, updated_at)
VALUES (3, 'ROLE_TEACHER', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 14. Admin user (password = BCrypt of "123456")
-- ============================================================
INSERT INTO tbl_users (id, username, email, password, full_name, phone, status, created_at, updated_at)
VALUES (
    3,
    'admin',
    'admin@myfschool.vn',
    '$2a$10$KU1x/wB.Z9Amz3RDUW/v0eSCl2i2N4ILVUnOHV8WJUgBsKpJsIUD2',
    'Quản Trị Viên',
    '0999000001',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- Admin - Role (fix: user_id = 3, không phải 100)
INSERT INTO user_roles (user_id, role_id)
VALUES (3, 2)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 15. Teacher User – Nguyễn Văn Toán (GV Toán, chủ nhiệm 12A1)
-- ============================================================
INSERT INTO tbl_users (id, username, email, password, full_name, phone, status, created_at, updated_at)
VALUES (
    4,
    'nguyenvantoan',
    'nguyenvantoan@myfschool.vn',
    '$2a$10$KU1x/wB.Z9Amz3RDUW/v0eSCl2i2N4ILVUnOHV8WJUgBsKpJsIUD2',
    'Nguyễn Văn Toán',
    '0901111111',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- Teacher User - Role
INSERT INTO user_roles (user_id, role_id)
VALUES (4, 3)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 16. Teacher Entity
-- ============================================================
INSERT INTO teachers (id, teacher_code, user_id, homeroom_class_id, subject_id, created_at, updated_at)
VALUES (
    1,
    'GV2025001',
    4,
    1,     -- chủ nhiệm lớp 12A1
    1,     -- môn Toán
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- Cập nhật lớp 12A1 → homeroom_teacher_id = 1
UPDATE class_rooms SET homeroom_teacher_id = 1 WHERE id = 1;

-- ============================================================
-- 17. Teacher 2 – Trần Văn Lý (GV Vật Lý, không chủ nhiệm)
-- ============================================================
INSERT INTO tbl_users (id, username, email, password, full_name, phone, status, created_at, updated_at)
VALUES (
    5,
    'tranvanly',
    'tranvanly@myfschool.vn',
    '$2a$10$KU1x/wB.Z9Amz3RDUW/v0eSCl2i2N4ILVUnOHV8WJUgBsKpJsIUD2',
    'Trần Văn Lý',
    '0902222222',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES (5, 3)
ON CONFLICT DO NOTHING;

INSERT INTO teachers (id, teacher_code, user_id, homeroom_class_id, subject_id, created_at, updated_at)
VALUES (
    2,
    'GV2025002',
    5,
    NULL,  -- không chủ nhiệm
    4,     -- môn Vật Lý
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 18. Update Schedules – gắn teacher_id
--     GV Toán (teacher_id=1) dạy các tiết Toán
--     GV Lý (teacher_id=2) dạy các tiết Vật Lý
-- ============================================================
UPDATE schedules SET teacher_id = 1 WHERE subject_id = 1;  -- Toán → GV Toán
UPDATE schedules SET teacher_id = 2 WHERE subject_id = 4;  -- Vật Lý → GV Lý

-- ============================================================
-- 19. Attendances (Điểm danh)
--     Lịch học hè: cuối tháng 6/2026 → tháng 9/2026
--     Hôm nay: 2026-07-02
--     Các ngày đã qua: có trạng thái điểm danh thực tế
--     Các ngày chưa đến: KHÔNG insert (chưa điểm danh)
-- ============================================================
INSERT INTO attendances (id, student_id, class_room_id, attendance_date, period, status, note, created_at, updated_at) VALUES
    -- ===== TUẦN 1: 22/06/2026 - 27/06/2026 =====
    -- 22/06 (Thứ 2) - Toán(1-2), Văn(3-4), Anh(5)
    (1,  1, 1, '2026-06-22', 1, 'PRESENT', NULL, NOW(), NOW()),
    (2,  1, 1, '2026-06-22', 2, 'PRESENT', NULL, NOW(), NOW()),
    (3,  1, 1, '2026-06-22', 3, 'PRESENT', NULL, NOW(), NOW()),
    (4,  1, 1, '2026-06-22', 4, 'PRESENT', NULL, NOW(), NOW()),
    (5,  1, 1, '2026-06-22', 5, 'PRESENT', NULL, NOW(), NOW()),
    (6,  2, 1, '2026-06-22', 1, 'PRESENT', NULL, NOW(), NOW()),
    (7,  2, 1, '2026-06-22', 2, 'PRESENT', NULL, NOW(), NOW()),
    (8,  2, 1, '2026-06-22', 3, 'LATE', 'Đi trễ 10 phút', NOW(), NOW()),
    (9,  2, 1, '2026-06-22', 4, 'PRESENT', NULL, NOW(), NOW()),
    (10, 2, 1, '2026-06-22', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- 23/06 (Thứ 3) - Lý(1-2), Hóa(3-4), Sinh(5)
    (11, 1, 1, '2026-06-23', 1, 'PRESENT', NULL, NOW(), NOW()),
    (12, 1, 1, '2026-06-23', 2, 'PRESENT', NULL, NOW(), NOW()),
    (13, 1, 1, '2026-06-23', 3, 'PRESENT', NULL, NOW(), NOW()),
    (14, 1, 1, '2026-06-23', 4, 'PRESENT', NULL, NOW(), NOW()),
    (15, 1, 1, '2026-06-23', 5, 'PRESENT', NULL, NOW(), NOW()),
    (16, 2, 1, '2026-06-23', 1, 'PRESENT', NULL, NOW(), NOW()),
    (17, 2, 1, '2026-06-23', 2, 'PRESENT', NULL, NOW(), NOW()),
    (18, 2, 1, '2026-06-23', 3, 'PRESENT', NULL, NOW(), NOW()),
    (19, 2, 1, '2026-06-23', 4, 'PRESENT', NULL, NOW(), NOW()),
    (20, 2, 1, '2026-06-23', 5, 'ABSENT_WITH_PERMISSION', 'Xin phép về sớm', NOW(), NOW()),

    -- 24/06 (Thứ 4) - Sử(1-2), Địa(3-4), GDCD(5)
    (21, 1, 1, '2026-06-24', 1, 'PRESENT', NULL, NOW(), NOW()),
    (22, 1, 1, '2026-06-24', 2, 'PRESENT', NULL, NOW(), NOW()),
    (23, 1, 1, '2026-06-24', 3, 'PRESENT', NULL, NOW(), NOW()),
    (24, 1, 1, '2026-06-24', 4, 'PRESENT', NULL, NOW(), NOW()),
    (25, 1, 1, '2026-06-24', 5, 'LATE', 'Đi trễ 5 phút', NOW(), NOW()),
    (26, 2, 1, '2026-06-24', 1, 'PRESENT', NULL, NOW(), NOW()),
    (27, 2, 1, '2026-06-24', 2, 'PRESENT', NULL, NOW(), NOW()),
    (28, 2, 1, '2026-06-24', 3, 'PRESENT', NULL, NOW(), NOW()),
    (29, 2, 1, '2026-06-24', 4, 'PRESENT', NULL, NOW(), NOW()),
    (30, 2, 1, '2026-06-24', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- 25/06 (Thứ 5) - Toán(1-2), Anh(3-4), Văn(5)
    (31, 1, 1, '2026-06-25', 1, 'PRESENT', NULL, NOW(), NOW()),
    (32, 1, 1, '2026-06-25', 2, 'PRESENT', NULL, NOW(), NOW()),
    (33, 1, 1, '2026-06-25', 3, 'PRESENT', NULL, NOW(), NOW()),
    (34, 1, 1, '2026-06-25', 4, 'PRESENT', NULL, NOW(), NOW()),
    (35, 1, 1, '2026-06-25', 5, 'PRESENT', NULL, NOW(), NOW()),
    (36, 2, 1, '2026-06-25', 1, 'PRESENT', NULL, NOW(), NOW()),
    (37, 2, 1, '2026-06-25', 2, 'PRESENT', NULL, NOW(), NOW()),
    (38, 2, 1, '2026-06-25', 3, 'PRESENT', NULL, NOW(), NOW()),
    (39, 2, 1, '2026-06-25', 4, 'PRESENT', NULL, NOW(), NOW()),
    (40, 2, 1, '2026-06-25', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- 26/06 (Thứ 6) - Hóa(1-2), Lý(3-4), Sinh(5)
    (41, 1, 1, '2026-06-26', 1, 'PRESENT', NULL, NOW(), NOW()),
    (42, 1, 1, '2026-06-26', 2, 'PRESENT', NULL, NOW(), NOW()),
    (43, 1, 1, '2026-06-26', 3, 'ABSENT_WITHOUT_PERMISSION', 'Nghỉ không phép', NOW(), NOW()),
    (44, 1, 1, '2026-06-26', 4, 'ABSENT_WITHOUT_PERMISSION', 'Nghỉ không phép', NOW(), NOW()),
    (45, 1, 1, '2026-06-26', 5, 'ABSENT_WITHOUT_PERMISSION', 'Nghỉ không phép', NOW(), NOW()),
    (46, 2, 1, '2026-06-26', 1, 'PRESENT', NULL, NOW(), NOW()),
    (47, 2, 1, '2026-06-26', 2, 'PRESENT', NULL, NOW(), NOW()),
    (48, 2, 1, '2026-06-26', 3, 'PRESENT', NULL, NOW(), NOW()),
    (49, 2, 1, '2026-06-26', 4, 'PRESENT', NULL, NOW(), NOW()),
    (50, 2, 1, '2026-06-26', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- 27/06 (Thứ 7) - Thể dục(1-2), GDQP(3-4), GDCD(5)
    (51, 1, 1, '2026-06-27', 1, 'PRESENT', NULL, NOW(), NOW()),
    (52, 1, 1, '2026-06-27', 2, 'PRESENT', NULL, NOW(), NOW()),
    (53, 1, 1, '2026-06-27', 3, 'PRESENT', NULL, NOW(), NOW()),
    (54, 1, 1, '2026-06-27', 4, 'PRESENT', NULL, NOW(), NOW()),
    (55, 1, 1, '2026-06-27', 5, 'PRESENT', NULL, NOW(), NOW()),
    (56, 2, 1, '2026-06-27', 1, 'PRESENT', NULL, NOW(), NOW()),
    (57, 2, 1, '2026-06-27', 2, 'PRESENT', NULL, NOW(), NOW()),
    (58, 2, 1, '2026-06-27', 3, 'PRESENT', NULL, NOW(), NOW()),
    (59, 2, 1, '2026-06-27', 4, 'PRESENT', NULL, NOW(), NOW()),
    (60, 2, 1, '2026-06-27', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- ===== TUẦN 2: 29/06/2026 - 04/07/2026 =====
    -- 29/06 (Thứ 2) - Toán(1-2), Văn(3-4), Anh(5)
    (61, 1, 1, '2026-06-29', 1, 'PRESENT', NULL, NOW(), NOW()),
    (62, 1, 1, '2026-06-29', 2, 'PRESENT', NULL, NOW(), NOW()),
    (63, 1, 1, '2026-06-29', 3, 'PRESENT', NULL, NOW(), NOW()),
    (64, 1, 1, '2026-06-29', 4, 'PRESENT', NULL, NOW(), NOW()),
    (65, 1, 1, '2026-06-29', 5, 'PRESENT', NULL, NOW(), NOW()),
    (66, 2, 1, '2026-06-29', 1, 'LATE', 'Đi trễ 15 phút', NOW(), NOW()),
    (67, 2, 1, '2026-06-29', 2, 'PRESENT', NULL, NOW(), NOW()),
    (68, 2, 1, '2026-06-29', 3, 'PRESENT', NULL, NOW(), NOW()),
    (69, 2, 1, '2026-06-29', 4, 'PRESENT', NULL, NOW(), NOW()),
    (70, 2, 1, '2026-06-29', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- 30/06 (Thứ 3) - Lý(1-2), Hóa(3-4), Sinh(5)
    (71, 1, 1, '2026-06-30', 1, 'PRESENT', NULL, NOW(), NOW()),
    (72, 1, 1, '2026-06-30', 2, 'PRESENT', NULL, NOW(), NOW()),
    (73, 1, 1, '2026-06-30', 3, 'PRESENT', NULL, NOW(), NOW()),
    (74, 1, 1, '2026-06-30', 4, 'PRESENT', NULL, NOW(), NOW()),
    (75, 1, 1, '2026-06-30', 5, 'PRESENT', NULL, NOW(), NOW()),
    (76, 2, 1, '2026-06-30', 1, 'PRESENT', NULL, NOW(), NOW()),
    (77, 2, 1, '2026-06-30', 2, 'PRESENT', NULL, NOW(), NOW()),
    (78, 2, 1, '2026-06-30', 3, 'PRESENT', NULL, NOW(), NOW()),
    (79, 2, 1, '2026-06-30', 4, 'PRESENT', NULL, NOW(), NOW()),
    (80, 2, 1, '2026-06-30', 5, 'PRESENT', NULL, NOW(), NOW()),

    -- 01/07 (Thứ 4) - Sử(1-2), Địa(3-4), GDCD(5)
    (81, 1, 1, '2026-07-01', 1, 'PRESENT', NULL, NOW(), NOW()),
    (82, 1, 1, '2026-07-01', 2, 'PRESENT', NULL, NOW(), NOW()),
    (83, 1, 1, '2026-07-01', 3, 'PRESENT', NULL, NOW(), NOW()),
    (84, 1, 1, '2026-07-01', 4, 'PRESENT', NULL, NOW(), NOW()),
    (85, 1, 1, '2026-07-01', 5, 'PRESENT', NULL, NOW(), NOW()),
    (86, 2, 1, '2026-07-01', 1, 'ABSENT_WITH_PERMISSION', 'Xin phép khám bệnh', NOW(), NOW()),
    (87, 2, 1, '2026-07-01', 2, 'ABSENT_WITH_PERMISSION', 'Xin phép khám bệnh', NOW(), NOW()),
    (88, 2, 1, '2026-07-01', 3, 'ABSENT_WITH_PERMISSION', 'Xin phép khám bệnh', NOW(), NOW()),
    (89, 2, 1, '2026-07-01', 4, 'ABSENT_WITH_PERMISSION', 'Xin phép khám bệnh', NOW(), NOW()),
    (90, 2, 1, '2026-07-01', 5, 'ABSENT_WITH_PERMISSION', 'Xin phép khám bệnh', NOW(), NOW()),

    -- 02/07 (Thứ 5 - HÔM NAY) - Toán(1-2), Anh(3-4), Văn(5)
    (91, 1, 1, '2026-07-02', 1, 'PRESENT', NULL, NOW(), NOW()),
    (92, 1, 1, '2026-07-02', 2, 'PRESENT', NULL, NOW(), NOW()),
    (93, 1, 1, '2026-07-02', 3, 'PRESENT', NULL, NOW(), NOW()),
    (94, 1, 1, '2026-07-02', 4, 'PRESENT', NULL, NOW(), NOW()),
    (95, 1, 1, '2026-07-02', 5, 'PRESENT', NULL, NOW(), NOW()),
    (96, 2, 1, '2026-07-02', 1, 'PRESENT', NULL, NOW(), NOW()),
    (97, 2, 1, '2026-07-02', 2, 'PRESENT', NULL, NOW(), NOW()),
    (98, 2, 1, '2026-07-02', 3, 'PRESENT', NULL, NOW(), NOW()),
    (99, 2, 1, '2026-07-02', 4, 'PRESENT', NULL, NOW(), NOW()),
    (100,2, 1, '2026-07-02', 5, 'PRESENT', NULL, NOW(), NOW())

    -- ===== TỪ 03/07/2026 TRỞ ĐI: CHƯA ĐẾN → KHÔNG INSERT ATTENDANCE =====
    -- Lịch học vẫn tiếp tục theo schedule đến hết tháng 9/2026
    -- Khi ngày đó đến, giáo viên sẽ điểm danh qua hệ thống

ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 20. Events (Sự kiện) – cập nhật cho hè 2026
-- ============================================================
INSERT INTO events (id, title, description, location, start_time, end_time, target_role, created_by, created_at, updated_at) VALUES
    (1,
     'Khai giảng khóa học hè 2026',
     'Lễ khai giảng chương trình học hè 2026. Toàn bộ học sinh tham gia.',
     'Hội trường A',
     '2026-06-22 07:30:00', '2026-06-22 09:00:00',
     'ALL', 3, NOW(), NOW()),

    (2,
     'Ngày hội thể thao hè',
     'Ngày hội thể dục thể thao hè, các lớp cử đại diện tham gia.',
     'Sân vận động trường',
     '2026-07-18 07:00:00', '2026-07-18 17:00:00',
     'ALL', 3, NOW(), NOW()),

    (3,
     'Họp phụ huynh giữa hè',
     'Giáo viên chủ nhiệm họp phụ huynh báo cáo tiến độ học hè.',
     'Phòng học các lớp',
     '2026-08-01 14:00:00', '2026-08-01 17:00:00',
     'TEACHER', 3, NOW(), NOW()),

    (4,
     'Thi cuối khóa hè',
     'Lịch thi cuối chương trình học hè cho tất cả các lớp.',
     'Các phòng thi',
     '2026-09-15 07:00:00', '2026-09-20 17:00:00',
     'ALL', 3, NOW(), NOW()),

    (5,
     'Lễ bế giảng khóa hè 2026',
     'Lễ bế giảng và tổng kết chương trình học hè 2026.',
     'Hội trường A',
     '2026-09-27 08:00:00', '2026-09-27 11:00:00',
     'ALL', 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Xác nhận dữ liệu đã insert
-- ============================================================
SELECT 'Seed data inserted successfully!' AS result;

INSERT INTO roles (id, name, created_at, updated_at)
VALUES (4, 'ROLE_PARENT', NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES (4, 4)
    ON CONFLICT DO NOTHING;

INSERT INTO parents (
    id,
    parent_code,
    user_id,
    occupation,
    relationship_note,
    created_at,
    updated_at
)
VALUES (
           1,
           'PH2025001',
           4,
           'Teacher',
           'Father of Nguyễn Văn A',
           NOW(),
           NOW()
       )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO parent_students (
    id,
    parent_id,
    student_id,
    relationship,
    emergency_contact,
    pickup_authorized
)
VALUES (
           1,
           1,
           1,
           'FATHER',
           true,
           true
       )
    ON CONFLICT (id) DO NOTHING;