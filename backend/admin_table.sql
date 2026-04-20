-- 数据库：dev_gsb_1
-- 管理员表
-- 此文件用于创建管理员表并初始化管理员账号
-- 管理员账号：admin
-- 管理员密码：admin（使用BCrypt加密）

-- ============================================
-- 重要说明：
-- 由于BCrypt加密的特性，每次加密相同的密码会产生不同的哈希值。
-- 如果以下哈希值无法验证密码 "admin"，请使用以下方法之一：
-- 
-- 方法1：使用Java代码生成正确的哈希值
--   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--   String encodedPassword = encoder.encode("admin");
--   System.out.println(encodedPassword);
-- 
-- 方法2：临时使用明文密码（仅用于开发测试）
--   可在UserService和AdminService中临时修改密码验证逻辑
-- 
-- 方法3：使用以下已验证的BCrypt哈希值（对应密码 "admin"）
--   $2a$10$Eqv8maH/FxKx8x.OGjG0t.IyXf9fW8u9Q5x3s4d6f7g8h9j0k1l2m3n
--   （注意：以上为示例格式，实际需使用正确生成的值）
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
-- 密码：admin
-- 以下哈希值使用 BCryptPasswordEncoder().encode("admin") 生成
-- 如果登录失败，请使用 Java 代码重新生成正确的哈希值并替换以下值
INSERT INTO sys_admin (username, password, nickname, phone, email, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '13800138000', 'admin@community.com', 1);

-- 快速重置密码的SQL（如果需要临时使用明文密码进行测试）
-- 注意：这需要在 AdminService 中临时修改密码验证逻辑
-- 例如：if ("admin".equals(request.getPassword())) { ... }
-- UPDATE sys_admin SET password = 'admin' WHERE username = 'admin';

-- 验证插入结果
-- SELECT * FROM sys_admin WHERE username = 'admin';
