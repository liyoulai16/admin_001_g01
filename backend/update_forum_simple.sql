-- ============================================
-- 论坛模块数据库更新 - 简化版（兼容所有MySQL版本）
-- 请按顺序执行以下SQL语句
-- ============================================

-- 1. 切换到目标数据库
USE dev_gsb_1;

-- 2. 检查并添加tag字段到forum_post表
-- 注意：如果已经存在tag字段，这条语句会报错，可以忽略
ALTER TABLE forum_post 
ADD COLUMN tag TINYINT DEFAULT 0 COMMENT '标签：0-普通帖，1-热门帖，2-置顶帖，3-热门且置顶帖' 
AFTER is_hot;

-- 3. 初始化tag字段（基于现有的is_hot字段）
UPDATE forum_post SET tag = 1 WHERE is_hot = 1;

-- 4. 创建帖子点赞记录表
DROP TABLE IF EXISTS forum_post_like;
CREATE TABLE forum_post_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '点赞用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞记录表';

-- 5. 创建评论表
DROP TABLE IF EXISTS forum_comment;
CREATE TABLE forum_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID，0表示对帖子的评论，非0表示对评论的回复',
    reply_to_id BIGINT DEFAULT 0 COMMENT '被回复的评论ID',
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

-- 6. 创建评论点赞记录表
DROP TABLE IF EXISTS forum_comment_like;
CREATE TABLE forum_comment_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '点赞用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    KEY idx_comment_id (comment_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞记录表';

-- 7. 插入测试数据（可选）
INSERT INTO forum_comment (post_id, user_id, parent_id, content) VALUES 
(1, 1, 0, '写得真好！我也参加了那次活动，确实很开心。'),
(1, 1, 0, '期待下次活动！我报名当志愿者帮忙组织。');

-- 插入测试回复
INSERT INTO forum_comment (post_id, user_id, parent_id, reply_to_id, reply_to_user_id, content) VALUES 
(1, 1, 1, 1, 1, '是啊是啊，希望以后能多组织这样的活动！');

-- ============================================
-- 执行完成！
-- ============================================
