-- 为现有表添加 record_status 字段（兼容MySQL 5.7版本）
-- 数据库：dev_gsb_1

USE dev_gsb_1;

-- ============================================
-- 方案1：如果数据不重要，直接删除后重建
-- ============================================
-- 先删除有外键依赖的子表
-- DROP TABLE IF EXISTS order_review;
-- DROP TABLE IF EXISTS service_appointment;
-- DROP TABLE IF EXISTS order_item;
-- DROP TABLE IF EXISTS service_order;
-- DROP TABLE IF EXISTS balance_transaction;
-- DROP TABLE IF EXISTS recharge_record;
-- DROP TABLE IF EXISTS user_balance;

-- 然后执行 order_recharge_appointment_tables.sql 重建表

-- ============================================
-- 方案2：手动添加字段（保留数据，兼容所有MySQL版本）
-- ============================================

-- 注意：如果字段已存在，执行以下命令会报错！
-- 报错信息：Duplicate column name 'record_status'
-- 这说明字段已经添加成功，可以忽略此错误

-- 1. 为 service_order 表添加 record_status 字段
ALTER TABLE service_order 
ADD COLUMN record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用';

-- 2. 为 service_appointment 表添加 record_status 字段
ALTER TABLE service_appointment 
ADD COLUMN record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用';

-- 3. 添加索引（可选，但建议添加）
ALTER TABLE service_order ADD INDEX idx_record_status (record_status);
ALTER TABLE service_appointment ADD INDEX idx_record_status (record_status);

-- 4. 更新现有数据的默认值（如果字段是 nullable 且已有数据）
UPDATE service_order SET record_status = 1 WHERE record_status IS NULL;
UPDATE service_appointment SET record_status = 1 WHERE record_status IS NULL;

-- 5. 验证字段是否添加成功
DESCRIBE service_order;
DESCRIBE service_appointment;
