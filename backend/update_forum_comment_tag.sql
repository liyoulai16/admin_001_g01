-- 数据库：dev_gsb_1
-- 论坛模块升级：评论、点赞、回复功能，帖子标签功能
-- 执行时间：2026-04-20

USE dev_gsb_1;

-- ============================================
-- 1. 修改帖子表，添加标签字段
-- ============================================
-- tag 字段说明：
--   0 - 普通帖（默认）
--   1 - 热门帖
--   2 - 置顶帖
--   3 - 热门且置顶帖
-- ============================================

ALTER TABLE forum_post 
ADD COLUMN IF NOT EXISTS tag TINYINT DEFAULT 0 COMMENT '标签：0-普通帖，1-热门帖，2-置顶帖，3-热门且置顶帖' 
AFTER is_hot;

-- 基于现有的 is_hot 和 is_essence 字段初始化 tag 字段
-- 注意：原 is_hot 字段表示热门，这里我们将其映射到新的 tag 字段
-- 置顶功能需要管理员手动设置

UPDATE forum_post SET tag = 1 WHERE is_hot = 1;

-- ============================================
-- 2. 创建帖子点赞记录表
-- 用于记录用户对帖子的点赞状态，避免重复点赞
-- ============================================

CREATE TABLE IF NOT EXISTS forum_post_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '点赞用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞记录表';

-- ============================================
-- 3. 创建评论表
-- 支持对帖子的评论和对评论的回复
-- ============================================

CREATE TABLE IF NOT EXISTS forum_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID，0表示对帖子的评论，非0表示对评论的回复',
    reply_to_id BIGINT DEFAULT 0 COMMENT '被回复的评论ID（当parent_id不为0时使用）',
    reply_to_user_id BIGINT DEFAULT NULL COMMENT '被回复的用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    likes INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-隐藏',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 4. 创建评论点赞记录表
-- 用于记录用户对评论的点赞状态
-- ============================================

CREATE TABLE IF NOT EXISTS forum_comment_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '点赞用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    KEY idx_comment_id (comment_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞记录表';

-- ============================================
-- 5. 插入测试数据
-- ============================================

-- 测试评论数据
INSERT INTO forum_comment (post_id, user_id, parent_id, content) VALUES 
(1, 1, 0, '写得真好！我也参加了那次活动，确实很开心。'),
(1, 1, 0, '期待下次活动！我报名当志愿者帮忙组织。'),
(1, 1, 0, '邻里关系就是要这样多交流，支持！');

-- 测试回复数据（对第一条评论的回复）
INSERT INTO forum_comment (post_id, user_id, parent_id, reply_to_id, reply_to_user_id, content) VALUES 
(1, 1, 1, 1, 1, '是啊是啊，希望以后能多组织这样的活动！');

-- ============================================
-- 完成说明
-- ============================================
-- 1. 新增字段 tag 到 forum_post 表
-- 2. 新增 forum_post_like 表记录用户帖子点赞
-- 3. 新增 forum_comment 表支持评论和回复
-- 4. 新增 forum_comment_like 表记录用户评论点赞
-- 5. tag 字段值：0-普通帖，1-热门帖，2-置顶帖，3-热门且置顶帖
-- ============================================
