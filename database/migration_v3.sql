-- ============================================
-- 房产管理系统 数据库迁移脚本 v3
-- 敏感数据加密：新增哈希列用于密文查找
-- ============================================

USE house_db;

-- 新增 SHA-256 哈希列（用于加密后邮箱/手机号的精确查找）
ALTER TABLE user ADD COLUMN email_hash VARCHAR(64) COMMENT 'SHA-256 hash of email for encrypted lookup';
ALTER TABLE user ADD COLUMN phone_hash VARCHAR(64) COMMENT 'SHA-256 hash of phone for encrypted lookup';

-- 为哈希列建索引以加速查找
ALTER TABLE user ADD INDEX idx_email_hash (email_hash);

-- 扩大 email、phone 列以容纳加密后的 Base64 密文
-- AES-256-GCM 加密后约 60-100 字符（Base64），预留足够空间
ALTER TABLE user MODIFY COLUMN email VARCHAR(255) COMMENT 'AES-256-GCM encrypted email';
ALTER TABLE user MODIFY COLUMN phone VARCHAR(255) COMMENT 'AES-256-GCM encrypted phone';
