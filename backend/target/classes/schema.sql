-- 数据库：dev_gsb_1
-- 用户表

CREATE DATABASE IF NOT EXISTS dev_gsb_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dev_gsb_1;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
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

-- 留言表
CREATE TABLE IF NOT EXISTS contact_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    type VARCHAR(50) DEFAULT NULL COMMENT '咨询类型：service-服务咨询, complaint-投诉建议, cooperation-商务合作, other-其他问题',
    message TEXT NOT NULL COMMENT '留言内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未处理，1-已处理，2-已回复',
    reply TEXT DEFAULT NULL COMMENT '回复内容',
    reply_by BIGINT DEFAULT NULL COMMENT '回复人ID（管理员ID）',
    reply_time DATETIME DEFAULT NULL COMMENT '回复时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_type (type),
    KEY idx_phone (phone),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系我们留言表';

-- 联系方式表
CREATE TABLE IF NOT EXISTS contact_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    icon VARCHAR(50) DEFAULT NULL COMMENT '图标',
    title VARCHAR(50) NOT NULL COMMENT '标题',
    detail VARCHAR(200) DEFAULT NULL COMMENT '详情',
    hint VARCHAR(200) DEFAULT NULL COMMENT '提示信息',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系方式表';

-- 插入默认联系方式数据
INSERT INTO contact_card (icon, title, detail, hint, sort_order, status) VALUES 
('📞', '客服热线', '400-123-4567', '工作时间：09:00 - 18:00', 1, 1),
('📧', '电子邮箱', 'service@community.com', '24小时内回复', 2, 1),
('📍', '公司地址', '幸福社区服务中心', '欢迎前来咨询', 3, 1),
('💬', '在线客服', '微信公众号', '扫码关注咨询', 4, 1);

-- 工作时间表
CREATE TABLE IF NOT EXISTS working_hour (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    day VARCHAR(50) NOT NULL COMMENT '星期',
    time VARCHAR(50) NOT NULL COMMENT '时间',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作时间表';

-- 插入默认工作时间数据
INSERT INTO working_hour (day, time, sort_order, status) VALUES 
('周一至周五', '09:00 - 18:00', 1, 1),
('周六', '09:00 - 17:00', 2, 1),
('周日', '10:00 - 16:00', 3, 1);

-- 常见问题表
CREATE TABLE IF NOT EXISTS faq (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    question VARCHAR(500) NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '答案',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常见问题表';

-- 插入默认常见问题数据
INSERT INTO faq (question, answer, sort_order, status) VALUES 
('如何预约服务？', '您可以通过我们的平台在线预约服务。选择您需要的服务项目，填写预约信息（日期、时间、联系方式等），提交后我们的客服会尽快与您确认预约详情。', 1, 1),
('服务价格如何确定？', '我们的服务价格明码标价，在服务详情页面可以看到具体的价格信息。部分服务可能根据实际情况有所调整，我们会在预约前与您确认最终价格。', 2, 1),
('如何取消或修改预约？', '如需取消或修改预约，请提前24小时联系我们的客服。您可以通过客服热线400-123-4567或在线客服进行操作。紧急情况下请直接拨打客服电话。', 3, 1),
('服务质量有问题怎么办？', '如果您对服务质量不满意，请及时联系我们的客服。我们会认真处理您的反馈，并根据情况提供退款、重新服务或其他解决方案。您的满意是我们最大的追求。', 4, 1),
('如何成为服务提供者？', '如果您想成为我们平台的服务提供者，请通过商务合作渠道联系我们。我们会对您的资质进行审核，审核通过后即可入驻平台提供服务。', 5, 1);

-- 特色优势表
CREATE TABLE IF NOT EXISTS feature (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    icon VARCHAR(50) DEFAULT NULL COMMENT '图标',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description VARCHAR(500) NOT NULL COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='特色优势表';

-- 插入默认特色优势数据
INSERT INTO feature (icon, title, description, sort_order, status) VALUES 
('⚡', '快速响应', '平均15分钟内回复您的咨询', 1, 1),
('🛡️', '安全保障', '您的个人信息严格保密', 2, 1),
('💯', '专业团队', '10年+社区服务经验', 3, 1),
('🌟', '用户好评', '98%的用户满意度', 4, 1);

-- 我们的使命表
CREATE TABLE IF NOT EXISTS about_mission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    image_url VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
    image_text VARCHAR(100) DEFAULT NULL COMMENT '图片说明文字',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='我们的使命表';

-- 插入默认使命数据
INSERT INTO about_mission (title, description, image_url, image_text, status) VALUES 
('我们的使命', '社区生活服务平台致力于为社区居民提供便捷、优质的生活服务。我们相信，通过整合社区资源，可以让每一位居民的生活更加便利、更加美好。\n\n我们的平台连接了社区居民与各类服务提供者，包括家政服务、维修服务、配送服务等。通过严格的服务质量把控和用户评价体系，我们确保每一项服务都能达到高标准。', '', '社区服务中心', 1);

-- 我们的价值观表
CREATE TABLE IF NOT EXISTS about_value (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    icon VARCHAR(50) DEFAULT NULL COMMENT '图标',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description VARCHAR(500) NOT NULL COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='我们的价值观表';

-- 插入默认价值观数据
INSERT INTO about_value (icon, title, description, sort_order, status) VALUES 
('🎯', '用户至上', '始终将用户需求放在首位，不断优化服务体验，让每一位用户都能感受到贴心的服务。', 1, 1),
('⭐', '品质保证', '严格筛选服务提供者，建立完善的质量监控体系，确保每一项服务都达到高品质标准。', 2, 1),
('🤝', '诚信经营', '坚持透明、公正的经营理念，明码标价，无隐藏费用，让用户消费得明明白白。', 3, 1),
('🚀', '持续创新', '不断探索新的服务模式和技术应用，为用户提供更加便捷、智能的生活服务体验。', 4, 1);

-- 平台数据表
CREATE TABLE IF NOT EXISTS about_stat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    number_text VARCHAR(50) NOT NULL COMMENT '数字显示文本（如：50,000+）',
    label VARCHAR(50) NOT NULL COMMENT '标签（如：注册用户）',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台数据表';

-- 插入默认平台数据
INSERT INTO about_stat (number_text, label, sort_order, status) VALUES 
('50,000+', '注册用户', 1, 1),
('1,000+', '服务提供者', 2, 1),
('100+', '服务项目', 3, 1),
('98%', '用户满意度', 4, 1);

-- 核心团队表
CREATE TABLE IF NOT EXISTS about_team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    avatar VARCHAR(50) DEFAULT NULL COMMENT '头像（emoji或URL）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    role VARCHAR(50) NOT NULL COMMENT '职位',
    bio VARCHAR(200) DEFAULT NULL COMMENT '简介',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='核心团队表';

-- 插入默认核心团队数据
INSERT INTO about_team (avatar, name, role, bio, sort_order, status) VALUES 
('👨‍💼', '张明', '创始人 & CEO', '拥有10年互联网行业经验，致力于打造最优质的社区生活服务平台。', 1, 1),
('👩‍💻', '李华', '技术总监', '资深全栈工程师，负责平台技术架构设计和产品开发。', 2, 1),
('👨‍🎨', '王强', '产品总监', '专注用户体验设计，致力于打造简洁易用的产品界面。', 3, 1),
('👩‍💼', '陈静', '运营总监', '负责平台运营和用户增长，确保服务质量和用户满意度。', 4, 1);

-- 公告表
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    summary VARCHAR(500) DEFAULT NULL COMMENT '公告摘要',
    cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已发布',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_is_pinned (is_pinned),
    KEY idx_sort_order (sort_order),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 插入示例公告数据
INSERT INTO announcement (title, content, summary, is_pinned, sort_order, status, view_count) VALUES 
('平台系统升级通知', '尊敬的用户：\n\n为了提供更好的服务体验，我们计划于本周六（2026年4月25日）凌晨02:00-05:00进行系统升级维护。\n\n升级期间，平台部分功能可能暂时无法使用，请您提前做好安排。\n\n给您带来的不便，敬请谅解！\n\n如有任何问题，请联系客服热线：400-123-4567\n\n社区生活服务平台\n2026年4月23日', '平台将于4月25日凌晨进行系统升级维护', 1, 0, 1, 156),
('新服务上线通知', '尊敬的用户：\n\n我们很高兴地宣布，平台新增了【宠物护理】服务！\n\n新服务包括：\n1. 宠物美容服务\n2. 宠物寄养服务\n3. 宠物医疗咨询\n4. 宠物训练课程\n\n欢迎广大宠物爱好者体验新服务！现在预约可享受首单8折优惠。\n\n预约方式：进入服务列表，选择【宠物护理】分类即可查看详情。\n\n社区生活服务平台\n2026年4月20日', '平台新增宠物护理服务，首单8折优惠', 1, 1, 1, 89),
('春季养生小贴士', '春季是万物复苏的季节，也是养生的好时机。以下是一些春季养生小贴士：\n\n1. 饮食宜清淡，多吃新鲜蔬菜和水果\n2. 适当增加户外活动，多晒太阳\n3. 保持充足睡眠，早睡早起\n4. 注意保暖，适时增减衣物\n5. 保持心情舒畅，避免过度焦虑\n\n希望大家都能拥有健康的身体和愉快的心情！\n\n社区生活服务平台\n2026年4月15日', '春季养生小贴士，让您健康度过每一天', 0, 0, 1, 234);
