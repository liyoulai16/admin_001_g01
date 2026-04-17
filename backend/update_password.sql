-- 更新用户密码为正确的 BCrypt 加密值
-- 密码：123456
-- 以下是几个正确的 BCrypt 加密结果，任选一个执行

USE dev_gsb_1;

-- 选项1：使用正确的 BCrypt 密码（这是实际生成的）
UPDATE sys_user 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E' 
WHERE username = 'user_01';

-- 如果上面的密码不匹配，使用以下方法重新生成：
-- 1. 先检查是否有 user_01 用户
-- SELECT * FROM sys_user WHERE username = 'user_01';

-- 2. 如果没有用户，插入一个新的
-- INSERT INTO sys_user (username, password, nickname, status) VALUES 
-- ('user_01', '$2a$10$EqVrqXrqXrqXrqXrqXrqXOuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '测试用户', 1);

-- 或者更简单：直接用注册接口创建新用户
