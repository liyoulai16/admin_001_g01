-- 数据库：dev_gsb_1
-- 轮播图表

USE dev_gsb_1;

-- 创建轮播图表
CREATE TABLE IF NOT EXISTS carousel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    description TEXT COMMENT '轮播图描述',
    image_url VARCHAR(500) NOT NULL COMMENT '图片链接',
    category VARCHAR(50) DEFAULT NULL COMMENT '关联的服务分类',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 插入轮播图数据
INSERT INTO carousel (title, description, image_url, category, sort_order, status) VALUES 
('家政保洁服务', '专业保洁团队，让您的家焕然一新。日常保洁、深度清洁、开荒保洁等多种服务可选。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20home%20cleaning%20service%20with%20cleaners%20working%20in%20modern%20apartment&image_size=landscape_16_9', '家政服务', 1, 1),
('家电维修服务', '经验丰富的维修师傅，快速解决您的家电问题。空调、冰箱、洗衣机等各类家电维修。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20appliance%20repair%20technician%20fixing%20air%20conditioner%20in%20modern%20home&image_size=landscape_16_9', '维修服务', 2, 1),
('教育培训服务', '优质的教育资源，助力孩子成长。课外辅导、兴趣培养、职业技能培训等多种课程。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20education%20tutoring%20service%20with%20teacher%20helping%20students%20in%20bright%20classroom&image_size=landscape_16_9', '教育培训', 3, 1),
('健康医疗服务', '专业的健康管理服务，守护您和家人的健康。在线问诊、健康咨询、体检预约等服务。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20healthcare%20medical%20service%20with%20doctor%20consulting%20patient%20in%20modern%20clinic&image_size=landscape_16_9', '健康医疗', 4, 1);
