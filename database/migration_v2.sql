-- ============================================
-- 房产管理系统 数据库迁移脚本 v2
--
-- 注意：本脚本只应执行一次。
-- 如果报 Duplicate column / Duplicate key 错误，
-- 说明已经迁移过了，忽略即可。
-- 如需全新安装请用 house_db.sql。
-- ============================================

USE house_db;

-- ============================================
-- 1. housing_record 表新增7列
--    （题目要求：户主、部门、职称、家庭人口、房号、住房面积 + 月租金）
-- ============================================
ALTER TABLE housing_record ADD COLUMN user_name   VARCHAR(50)  COMMENT '户主姓名';
ALTER TABLE housing_record ADD COLUMN department  VARCHAR(50)  COMMENT '部门';
ALTER TABLE housing_record ADD COLUMN title_level VARCHAR(50)  COMMENT '职称';
ALTER TABLE housing_record ADD COLUMN family_size INT           COMMENT '家庭人口';
ALTER TABLE housing_record ADD COLUMN house_title VARCHAR(100)  COMMENT '房号';
ALTER TABLE housing_record ADD COLUMN house_area  DOUBLE        COMMENT '住房面积（㎡）';
ALTER TABLE housing_record ADD COLUMN rent_amount DOUBLE        COMMENT '月租金（元）';

-- 回填已有数据
UPDATE housing_record hr
    JOIN user u ON hr.user_id = u.id
    JOIN house h ON hr.house_id = h.id
SET hr.user_name  = u.name,
    hr.house_title = h.title,
    hr.house_area  = h.area
WHERE hr.user_name IS NULL;

-- ============================================
-- 2. application 表新增1列（原住房面积）
-- ============================================
ALTER TABLE application ADD COLUMN original_house_area DOUBLE COMMENT '原住房面积（调房用）';

-- ============================================
-- 3. 新建 rent 表（房租表）
-- ============================================
CREATE TABLE IF NOT EXISTS rent (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT    NOT NULL COMMENT '住户ID',
    house_id    INT    NOT NULL COMMENT '房屋ID',
    rent_amount DOUBLE NOT NULL COMMENT '月租金（元）',
    rent_date   DATE            COMMENT '租金计算日期',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- ============================================
-- 4. 补充索引
-- ============================================
ALTER TABLE house          ADD INDEX idx_house_status (status);
ALTER TABLE application    ADD INDEX idx_app_status (status);
ALTER TABLE application    ADD INDEX idx_app_type_status (type, status);
ALTER TABLE housing_record ADD INDEX idx_record_user (user_id);
ALTER TABLE rent           ADD INDEX idx_rent_house (house_id);