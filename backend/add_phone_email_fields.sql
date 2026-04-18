-- 为用户表添加手机号码和邮箱字段
-- 执行此脚本以更新现有数据库

USE dev_gsb_1;

-- 添加手机号码字段
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码' 
AFTER nickname;

-- 添加邮箱字段
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS email VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址' 
AFTER phone;

-- 查看表结构确认添加成功
DESCRIBE sys_user;
