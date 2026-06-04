CREATE DATABASE house_db;
USE house_db;

-- 用户表
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    role VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- 房屋表
CREATE TABLE house (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    status VARCHAR(20),

    area DOUBLE,
    rent_per_m2 DOUBLE
);

-- 申请表（分房/调房/退房）
CREATE TABLE application (
    id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT,
    house_id INT,

    type VARCHAR(20),      -- ALLOCATE / TRANSFER / RETURN
    status VARCHAR(20),    -- PENDING / APPROVED / REJECTED

    department VARCHAR(50),
    title_level VARCHAR(50),

    family_size INT,
    score INT,

    request_area DOUBLE,

    original_house_id INT,

    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- 住房标准表
CREATE TABLE housing_standard (
    id INT PRIMARY KEY AUTO_INCREMENT,

    area DOUBLE,
    min_score INT
);

-- 已分配住房记录表
CREATE TABLE housing_record (
    id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT,
    house_id INT,

    score INT,

    move_in_date DATE,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- 用户测试数据
INSERT INTO user (id, name, role, phone, email)
VALUES
(1,'张三','owner','13800000000','zhangsan@test.com'),
(2,'李四','tenant','13811111111','lisi@test.com');

-- 房屋测试数据
INSERT INTO house (title, status, area, rent_per_m2)
VALUES
('A101','空房',50,10),
('A102','空房',80,12),
('B201','已分配',120,15);

-- 住房标准测试数据
INSERT INTO housing_standard (area, min_score)
VALUES
(50,60),
(80,80),
(120,100);

-- 已分配住房记录测试数据
INSERT INTO housing_record (user_id, house_id, score, move_in_date)
VALUES
(1,3,105,CURDATE());

-- 申请测试数据
INSERT INTO application
(user_id, house_id, type, status, department, title_level, family_size, score, request_area)
VALUES
(2,NULL,'ALLOCATE','PENDING','计算机学院','讲师',3,85,80);