-- 数据库：dev_gsb_1
-- 平台简介表

USE dev_gsb_1;

-- 创建平台简介表
CREATE TABLE IF NOT EXISTS platform_intro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(100) DEFAULT NULL COMMENT '标题',
    content TEXT COMMENT '内容（支持换行）',
    image_url VARCHAR(500) DEFAULT NULL COMMENT '图片链接',
    stat1_label VARCHAR(50) DEFAULT NULL COMMENT '统计项1标签',
    stat1_value VARCHAR(50) DEFAULT NULL COMMENT '统计项1数值',
    stat2_label VARCHAR(50) DEFAULT NULL COMMENT '统计项2标签',
    stat2_value VARCHAR(50) DEFAULT NULL COMMENT '统计项2数值',
    stat3_label VARCHAR(50) DEFAULT NULL COMMENT '统计项3标签',
    stat3_value VARCHAR(50) DEFAULT NULL COMMENT '统计项3数值',
    stat4_label VARCHAR(50) DEFAULT NULL COMMENT '统计项4标签',
    stat4_value VARCHAR(50) DEFAULT NULL COMMENT '统计项4数值',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台简介表';

-- 插入平台简介数据（使用Unsplash主题相关稳定图片）
INSERT INTO platform_intro (title, content, image_url, stat1_label, stat1_value, stat2_label, stat2_value, stat3_label, stat3_value, stat4_label, stat4_value, status) VALUES 
('平台简介', 
'社区生活服务平台致力于为您提供全方位的社区生活服务解决方案。我们汇聚了社区周边各类优质服务资源，从家政保洁、维修服务到教育培训、健康医疗，让您足不出户就能享受到便捷、高效、专业的服务体验。
我们的使命是让社区生活更便捷、更温暖。通过智能化的服务匹配系统，我们能够快速为您找到最适合的服务提供商，同时提供安全保障和质量监督，确保每一次服务都让您满意。
加入我们，体验全新的社区生活方式。无论是日常生活中的小事，还是重要的家庭需求，我们都将竭诚为您服务，让您的生活更加轻松愉快。', 
'https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=800&h=500&fit=crop&auto=format', 
'服务项目', '1000+', 
'合作商家', '500+', 
'服务用户', '10万+', 
'好评率', '98%', 
1);
