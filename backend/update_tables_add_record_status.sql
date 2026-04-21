-- 为现有表添加 record_status 字段
-- 数据库：dev_gsb_1

USE dev_gsb_1;

-- 方案1：如果表中数据不重要，建议删除后重建
-- DROP TABLE IF EXISTS service_appointment;
-- DROP TABLE IF EXISTS service_order;
-- 然后重新执行 order_recharge_appointment_tables.sql

-- 方案2：使用 ALTER TABLE 添加字段（保留现有数据）

-- 1. 为 service_order 表添加 record_status 字段
-- 首先检查字段是否存在，不存在则添加
ALTER TABLE service_order 
ADD COLUMN IF NOT EXISTS record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用' 
AFTER remark;

-- 添加索引
ALTER TABLE service_order ADD INDEX IF NOT EXISTS idx_record_status (record_status);

-- 2. 为 service_appointment 表添加 record_status 字段
ALTER TABLE service_appointment 
ADD COLUMN IF NOT EXISTS record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用' 
AFTER remark;

-- 添加索引
ALTER TABLE service_appointment ADD INDEX IF NOT EXISTS idx_record_status (record_status);

-- 3. 为现有数据设置默认值（如果字段已存在但没有默认值）
UPDATE service_order SET record_status = 1 WHERE record_status IS NULL;
UPDATE service_appointment SET record_status = 1 WHERE record_status IS NULL;
