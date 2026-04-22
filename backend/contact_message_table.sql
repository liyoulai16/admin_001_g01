-- 留言表
-- 用于存储用户通过联系我们页面提交的在线留言

USE dev_gsb_1;

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
