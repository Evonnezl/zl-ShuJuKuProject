DROP DATABASE IF EXISTS house_db;
CREATE DATABASE house_db;
USE house_db;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    role VARCHAR(20),     -- 'owner'（管理员）| 'tenant'（住户）
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- ============================================
-- 2. 房产表（房屋表）
--    题目要求：房号、住房面积、分配标志、每平方米房租
-- ============================================
CREATE TABLE IF NOT EXISTS house (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),       -- 房号
    status VARCHAR(20),       -- 分配标志：'空房' | '已分配'
    area DOUBLE,              -- 住房面积（㎡）
    rent_per_m2 DOUBLE        -- 每平方米月租金（元）
);

-- ============================================
-- 3. 申请表（分房/调房/退房）
--    题目要求：住房要求 = 户主 + [分房要求|调房要求|退房要求]
-- ============================================
CREATE TABLE IF NOT EXISTS application (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    house_id INT,
    type VARCHAR(20),            -- ALLOCATE（分房）/ TRANSFER（调房）/ RETURN（退房）
    status VARCHAR(20),          -- PENDING / APPROVED / REJECTED
    department VARCHAR(50),      -- 部门
    title_level VARCHAR(50),     -- 职称
    family_size INT,             -- 家庭人口
    score INT,                   -- 住房分数
    request_area DOUBLE,         -- 要求住房面积
    original_house_id INT,       -- 原房号（调房用）
    original_house_area DOUBLE,  -- 原住房面积（调房用）
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- ============================================
-- 4. 住房标准表
--    题目要求：住房面积、最低住房分数（按面积递增排列）
-- ============================================
CREATE TABLE IF NOT EXISTS housing_standard (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area DOUBLE,        -- 住房面积（㎡）
    min_score INT       -- 最低住房分数
);

-- ============================================
-- 5. 住房表（已分配住房记录）
--    题目要求：户主、部门、职称、家庭人口、住房分数、房号、住房面积
--    额外包含：月租金（方便查询）
-- ============================================
CREATE TABLE IF NOT EXISTS housing_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,              -- 住户ID
    house_id INT,             -- 房屋ID
    score INT,                -- 住房分数
    move_in_date DATE,        -- 入住日期
    -- 以下为题目要求的冗余字段（分配时快照）
    user_name VARCHAR(50),    -- 户主
    department VARCHAR(50),   -- 部门
    title_level VARCHAR(50),  -- 职称
    family_size INT,          -- 家庭人口
    house_title VARCHAR(100), -- 房号
    house_area DOUBLE,        -- 住房面积（㎡）
    rent_amount DOUBLE,       -- 月租金（元）
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- ============================================
-- 6. 房租表（题目要求：分房后计算房租写入房租文件）
--    房租 = 房屋面积 × 每平方米月租金
-- ============================================
CREATE TABLE IF NOT EXISTS rent (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,           -- 住户ID
    house_id INT NOT NULL,          -- 房屋ID
    rent_amount DOUBLE NOT NULL,    -- 月租金（元）
    rent_date DATE,                 -- 租金计算日期
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- ============================================
-- 索引
-- ============================================
CREATE INDEX idx_house_status    ON house(status);
CREATE INDEX idx_app_status      ON application(status);
CREATE INDEX idx_app_type_status ON application(type, status);
CREATE INDEX idx_record_user     ON housing_record(user_id);
CREATE INDEX idx_rent_house      ON rent(house_id);

-- ============================================
-- 测试数据
-- ============================================

-- 用户
INSERT INTO user (id, name, role, phone, email) VALUES
(1, '张三', 'owner',  '13800000000', 'zhangsan@test.com'),
(2, '李四', 'tenant', '13811111111', 'lisi@test.com');

-- 房屋
INSERT INTO house (title, status, area, rent_per_m2) VALUES
('A101', '空房',   50,  10),
('A102', '空房',   80,  12),
('B201', '已分配', 120, 15);

-- 住房标准
INSERT INTO housing_standard (area, min_score) VALUES
(50,  60),
(80,  80),
(120, 100);

-- 已分配住房记录（张三分配到B201）
INSERT INTO housing_record
(user_id, house_id, score, move_in_date, user_name, department, title_level, family_size, house_title, house_area, rent_amount)
VALUES
(1, 3, 105, CURDATE(), '张三', '计算机学院', '教授', 4, 'B201', 120, 1800);

-- 申请（李四申请分房）
INSERT INTO application
(user_id, house_id, type, status, department, title_level, family_size, score, request_area)
VALUES
(2, NULL, 'ALLOCATE', 'PENDING', '计算机学院', '讲师', 3, 85, 80);