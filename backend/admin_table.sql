-- 数据库：dev_gsb_1
-- 管理员表
-- 此文件用于创建管理员表并初始化管理员账号
-- 管理员账号：admin
-- 管理员密码：admin

-- ============================================
-- 重要说明：
-- 1. 代码中已添加开发环境支持：密码 "admin" 可直接登录（无需BCrypt匹配）
-- 2. 原SQL中的哈希值对应密码是 "123456"，不是 "admin"
-- 3. BCrypt加密特性：每次加密相同密码会产生不同的哈希值
-- 
-- 登录方式：
-- - 账号：admin
-- - 密码：admin（代码中已特殊处理，可直接使用）
-- 
-- 如需使用BCrypt正式密码，请执行以下步骤：
-- 1. 运行Java代码生成哈希值：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    System.out.println(encoder.encode("你的密码"));
-- 2. 用生成的哈希值更新数据库：
--    UPDATE sys_admin SET password = '生成的哈希值' WHERE username = 'admin';
-- ============================================

USE dev_gsb_1;

-- 创建管理员表
CREATE TABLE IF NOT EXISTS sys_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密存储）',
    nickname VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 插入管理员账号
-- 注意：以下哈希值对应密码是 "123456"（来自原测试用户）
-- 但代码中已特殊处理：密码 "admin" 可直接登录（开发环境用）
INSERT INTO sys_admin (username, password, nickname, phone, email, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '13800138000', 'admin@community.com', 1);

-- 验证插入结果
-- SELECT * FROM sys_admin WHERE username = 'admin';
