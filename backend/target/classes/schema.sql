-- 数据库：dev_gsb_1
-- 用户表

CREATE DATABASE IF NOT EXISTS dev_gsb_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dev_gsb_1;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试用户（密码：123456，使用BCrypt加密）
-- 注意：BCrypt每次加密结果不同，以下是几个示例，都可以用123456验证
INSERT INTO sys_user (username, password, nickname, status) VALUES 
('user_01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '测试用户', 1);

-- 服务分类表
CREATE TABLE IF NOT EXISTS service_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(50) DEFAULT NULL COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name (name),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务分类表';

-- 服务项目表
CREATE TABLE IF NOT EXISTS service_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '服务名称',
    icon VARCHAR(50) DEFAULT NULL COMMENT '服务图标',
    description TEXT DEFAULT NULL COMMENT '服务描述',
    price VARCHAR(50) DEFAULT NULL COMMENT '价格显示文本',
    price_value DECIMAL(10, 2) DEFAULT 0.00 COMMENT '价格数值（用于排序）',
    rating DECIMAL(2, 1) DEFAULT 0.0 COMMENT '评分',
    reviews INT DEFAULT 0 COMMENT '评价数量',
    features JSON DEFAULT NULL COMMENT '服务特点（JSON数组）',
    details TEXT DEFAULT NULL COMMENT '详细描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_rating (rating),
    KEY idx_price_value (price_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务项目表';

-- 插入服务分类数据
INSERT INTO service_category (name, icon, sort_order, status) VALUES 
('家政服务', '🏠', 1, 1),
('维修服务', '🔧', 2, 1),
('配送服务', '🚚', 3, 1),
('社区服务', '👥', 4, 1),
('便民服务', '🛠️', 5, 1),
('生活服务', '🌟', 6, 1);

-- 插入服务项目数据
INSERT INTO service_item (category_id, name, icon, description, price, price_value, rating, reviews, features, details, sort_order, status) VALUES 
(1, '家政保洁', '🧹', '专业的家庭保洁服务，让您的家焕然一新。包括日常保洁、深度清洁、厨房清洁、卫生间清洁等。', '¥50起/小时', 50.00, 4.8, 256, '["专业保洁人员", "环保清洁用品", "满意保障", "灵活预约"]', '我们的家政保洁服务由经过专业培训的保洁人员提供，使用环保清洁用品，确保您的家庭环境健康安全。服务包括：日常保洁、深度清洁、厨房清洁、卫生间清洁、玻璃清洁等。', 1, 1),
(2, '家电维修', '🔧', '专业家电维修服务，快速上门，解决您的家电问题。包括空调、冰箱、洗衣机、电视等维修。', '¥80起', 80.00, 4.9, 189, '["专业技师", "快速上门", "原厂配件", "质保服务"]', '我们的家电维修服务由经验丰富的专业技师提供，快速上门诊断和维修各种家电问题。包括空调维修、冰箱维修、洗衣机维修、电视维修、热水器维修等。', 1, 1),
(3, '生鲜配送', '🥬', '新鲜食材，快速配送，让您足不出户享受新鲜美味。包括蔬菜、水果、肉类、海鲜等配送。', '免费配送（满¥39）', 0.00, 4.7, 423, '["新鲜直达", "快速配送", "品质保证", "货到付款"]', '我们的生鲜配送服务为您提供新鲜的蔬菜、水果、肉类、海鲜等食材，从产地直达您的餐桌。每日新鲜采购，确保食材品质。支持货到付款和在线支付。', 1, 1),
(4, '社区活动', '🎉', '丰富多彩的社区活动，增进邻里关系，丰富业余生活。包括亲子活动、健身课程、文化讲座等。', '免费/低收费', 0.00, 4.6, 156, '["丰富多彩", "邻里互动", "专业指导", "安全保障"]', '我们定期组织各种社区活动，包括亲子活动、健身课程、文化讲座、手工制作、节日庆祝等。活动由专业人员指导，安全有保障，欢迎社区居民积极参与。', 1, 1),
(5, '快递代收', '📦', '便捷的快递代收服务，让您不再错过任何包裹。支持多家快递公司，安全可靠。', '免费', 0.00, 4.5, 312, '["安全可靠", "多家快递", "随时取件", "短信通知"]', '我们的快递代收服务为您提供安全可靠的包裹代收服务。支持顺丰、圆通、中通、申通、韵达等多家快递公司。包裹到达后会有短信通知，您可以随时前来取件。', 1, 1),
(6, '宠物服务', '🐕', '专业的宠物服务，包括宠物寄养、宠物美容、宠物医疗等。让您的爱宠得到专业照顾。', '¥30起', 30.00, 4.8, 198, '["专业护理", "安全环境", "24小时监护", "健康检查"]', '我们的宠物服务包括宠物寄养、宠物美容、宠物医疗、宠物训练等。由专业的宠物护理人员提供服务，确保您的爱宠得到专业、细心的照顾。', 1, 1);
