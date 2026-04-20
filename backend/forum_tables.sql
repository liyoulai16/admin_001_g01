-- 数据库：dev_gsb_1
-- 论坛模块表结构

USE dev_gsb_1;

-- 论坛版块分类表
CREATE TABLE IF NOT EXISTS forum_category (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛版块分类表';

-- 论坛帖子表
CREATE TABLE IF NOT EXISTS forum_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容',
    excerpt VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    images TEXT DEFAULT NULL COMMENT '图片URL列表（JSON数组）',
    views INT DEFAULT 0 COMMENT '浏览量',
    comments INT DEFAULT 0 COMMENT '评论数',
    likes INT DEFAULT 0 COMMENT '点赞数',
    is_essence TINYINT DEFAULT 0 COMMENT '是否精华：0-否，1-是',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-隐藏',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_category_id (category_id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    KEY idx_views (views),
    KEY idx_is_essence (is_essence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子表';

-- 插入论坛分类数据
INSERT INTO forum_category (name, icon, sort_order, status) VALUES 
('生活分享', '🏠', 1, 1),
('互助问答', '❓', 2, 1),
('邻里活动', '🎉', 3, 1),
('二手交易', '🛒', 4, 1);

-- 插入测试帖子数据
INSERT INTO forum_post (category_id, user_id, title, content, excerpt, views, comments, likes, is_essence, is_hot, status) VALUES 
(1, 1, '【生活分享】周末组织了小区烧烤活动，大家都很开心！', '上周末我们单元组织了一次烧烤聚会，大家一起准备食材、生火烤肉，气氛非常热闹。小朋友们在旁边玩游戏，大人们聊天交流，增进了邻里之间的感情。希望以后能多组织这样的活动！', '上周末我们单元组织了一次烧烤聚会，大家一起准备食材、生火烤肉，气氛非常热闹...', 356, 28, 45, 1, 1, 1),
(2, 1, '【互助问答】求助：家里水管漏水了，有靠谱的维修师傅推荐吗？', '厨房的水管突然漏水了，想找个靠谱的维修师傅，请问邻里们有推荐的吗？最好是价格公道、技术好的，谢谢大家！', '厨房的水管突然漏水了，想找个靠谱的维修师傅，请问邻里们有推荐的吗？...', 234, 15, 12, 0, 0, 1),
(3, 1, '【邻里活动】小区春季运动会报名开始啦！', '一年一度的小区春季运动会即将举行，现面向全体业主开放报名。比赛项目包括：趣味接力、亲子游戏、拔河比赛等，欢迎大家踊跃参与！', '一年一度的小区春季运动会即将举行，现面向全体业主开放报名...', 567, 42, 78, 1, 1, 1),
(4, 1, '【二手交易】九成新婴儿车转让，价格可议', '家里宝宝长大了，婴儿车用不上了，转让给有需要的邻里。品牌是好孩子的，九成新，原价1200，现在400出，可小刀。有意者请在评论区留言或私信我。', '家里宝宝长大了，婴儿车用不上了，转让给有需要的邻里...', 189, 8, 5, 0, 0, 1),
(1, 1, '【生活分享】小区的腊梅花开了，真漂亮！', '今早路过小区花园，发现腊梅花开得正盛，香气扑鼻。拍了几张照片和大家分享，也提醒大家注意保暖，天气冷了多穿点衣服。', '今早路过小区花园，发现腊梅花开得正盛，香气扑鼻...', 423, 35, 56, 0, 0, 1);
