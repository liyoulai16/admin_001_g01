-- 优惠券系统表
-- 数据库：dev_gsb_1

USE dev_gsb_1;

-- 优惠券模板表
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    type VARCHAR(20) NOT NULL DEFAULT 'full_reduction' COMMENT '类型：full_reduction-满减券，discount-折扣券',
    discount_amount DECIMAL(10, 2) DEFAULT NULL COMMENT '减免金额（满减券使用）',
    discount_percent DECIMAL(3, 2) DEFAULT NULL COMMENT '折扣比例（折扣券使用，如0.85表示8.5折）',
    max_discount DECIMAL(10, 2) DEFAULT NULL COMMENT '最高减免金额（折扣券使用）',
    min_spend DECIMAL(10, 2) DEFAULT 0.00 COMMENT '最低消费金额（0表示无门槛）',
    total_quantity INT DEFAULT 0 COMMENT '发放总量',
    claimed_quantity INT DEFAULT 0 COMMENT '已领取数量',
    per_user_limit INT DEFAULT 1 COMMENT '每人限领数量（0表示不限）',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类标识：browse_reward-浏览奖励，new_user-新用户，promotion-促销，member-会员专享',
    description TEXT DEFAULT NULL COMMENT '优惠券描述',
    valid_from DATE NOT NULL COMMENT '有效期开始日期',
    valid_to DATE NOT NULL COMMENT '有效期结束日期',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-启用，2-过期，3-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_type (type),
    KEY idx_category (category),
    KEY idx_status (status),
    KEY idx_valid_from (valid_from),
    KEY idx_valid_to (valid_to),
    KEY idx_sort_order (sort_order),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券模板表';

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS user_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券模板ID',
    coupon_name VARCHAR(100) NOT NULL COMMENT '优惠券名称（冗余存储）',
    type VARCHAR(20) NOT NULL COMMENT '类型：full_reduction-满减券，discount-折扣券',
    discount_amount DECIMAL(10, 2) DEFAULT NULL COMMENT '减免金额',
    discount_percent DECIMAL(3, 2) DEFAULT NULL COMMENT '折扣比例',
    max_discount DECIMAL(10, 2) DEFAULT NULL COMMENT '最高减免金额',
    min_spend DECIMAL(10, 2) DEFAULT 0.00 COMMENT '最低消费金额',
    valid_from DATE NOT NULL COMMENT '有效期开始日期',
    valid_to DATE NOT NULL COMMENT '有效期结束日期',
    order_id BIGINT DEFAULT NULL COMMENT '使用订单ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-可用，2-已使用，3-已过期',
    delete TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    used_time DATETIME DEFAULT NULL COMMENT '使用时间',
    KEY idx_user_id (user_id),
    KEY idx_coupon_id (coupon_id),
    KEY idx_status (status),
    KEY idx_valid_to (valid_to),
    KEY idx_order_id (order_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';

-- 浏览记录表
CREATE TABLE IF NOT EXISTS browse_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    browse_date DATE NOT NULL COMMENT '浏览日期',
    total_seconds INT DEFAULT 0 COMMENT '今日累计浏览时长（秒）',
    is_reward_claimed TINYINT DEFAULT 0 COMMENT '是否已领取今日奖励：0-未领取，1-已领取',
    reward_coupon_id BIGINT DEFAULT NULL COMMENT '领取的优惠券ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_date (user_id, browse_date),
    KEY idx_user_id (user_id),
    KEY idx_browse_date (browse_date),
    KEY idx_is_reward_claimed (is_reward_claimed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览记录表';

-- 插入示例优惠券数据
INSERT INTO coupon (name, type, discount_amount, min_spend, total_quantity, per_user_limit, category, description, valid_from, valid_to, sort_order, status) VALUES 
('浏览奖励10元券', 'full_reduction', 10.00, 50.00, 1000, 1, '浏览奖励', '浏览服务列表满1分钟即可领取，满50元可减10元', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d'), 1, 1),
('浏览奖励20元券', 'full_reduction', 20.00, 100.00, 500, 1, '浏览奖励', '浏览服务列表满1分钟即可领取，满100元可减20元', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d'), 2, 1),
('浏览奖励5元无门槛', 'full_reduction', 5.00, 0.00, 2000, 1, '浏览奖励', '浏览服务列表满1分钟即可领取，无门槛使用', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d'), 3, 1),
('浏览奖励85折券', 'discount', NULL, 0.85, 50.00, 800, 1, '浏览奖励', '浏览服务列表满1分钟即可领取，全场85折，最高减免50元', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d'), 4, 1),
('新用户专享券', 'full_reduction', 15.00, 30.00, 1000, 1, '新用户', '新用户注册专享，满30元减15元', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d'), 5, 1),
('节日特惠券', 'full_reduction', 30.00, 150.00, 300, 1, '节日活动', '节日特惠，满150元减30元', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 7 DAY), '%Y-%m-%d'), 6, 1);

-- 插入折扣券示例数据
INSERT INTO coupon (name, type, discount_percent, max_discount, min_spend, total_quantity, per_user_limit, category, description, valid_from, valid_to, sort_order, status) VALUES 
('会员专享8折券', 'discount', 0.80, 100.00, 0.00, 200, 1, '会员专享', '会员专享优惠，全场8折，最高减免100元', DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 60 DAY), '%Y-%m-%d'), 7, 1);
