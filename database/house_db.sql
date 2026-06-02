CREATE DATABASE house_db;
USE house_db;

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    role VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE house (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    status VARCHAR(20)
);

CREATE TABLE application (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    house_id INT,
    type VARCHAR(20),
    status VARCHAR(20)
);

INSERT INTO user VALUES
(1,'张三','owner','13800000000','zhangsan@test.com'),
(2,'李四','tenant','13811111111','lisi@test.com');