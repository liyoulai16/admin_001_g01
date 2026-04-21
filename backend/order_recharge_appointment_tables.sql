-- 订单、充值、服务预约相关表结构
-- 数据库：dev_gsb_1

USE dev_gsb_1;

-- 1. 用户余额表
CREATE TABLE IF NOT EXISTS user_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    balance DECIMAL(10, 2) DEFAULT 0.00 COMMENT '账户余额',
    frozen_balance DECIMAL(10, 2) DEFAULT 0.00 COMMENT '冻结余额',
    total_recharge DECIMAL(10, 2) DEFAULT 0.00 COMMENT '累计充值金额',
    total_spend DECIMAL(10, 2) DEFAULT 0.00 COMMENT '累计消费金额',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_record_status (record_status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户余额表';

-- 2. 充值记录表
CREATE TABLE IF NOT EXISTS recharge_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    recharge_no VARCHAR(32) NOT NULL COMMENT '充值订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '充值金额',
    pay_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实际支付金额',
    bonus_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '赠送金额',
    pay_method TINYINT DEFAULT 0 COMMENT '支付方式：0-微信支付，1-支付宝，2-银行卡',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-已取消',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    transaction_id VARCHAR(64) DEFAULT NULL COMMENT '第三方交易流水号',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_recharge_no (recharge_no),
    KEY idx_user_id (user_id),
    KEY idx_pay_status (pay_status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- 3. 订单表
CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    service_id BIGINT NOT NULL COMMENT '服务项目ID',
    category_id BIGINT NOT NULL COMMENT '服务分类ID',
    service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
    category_name VARCHAR(50) DEFAULT NULL COMMENT '分类名称',
    service_icon VARCHAR(50) DEFAULT NULL COMMENT '服务图标',
    service_image VARCHAR(255) DEFAULT NULL COMMENT '服务图片',
    service_description TEXT DEFAULT NULL COMMENT '服务描述',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '服务单价',
    quantity INT DEFAULT 1 COMMENT '数量',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    extra_fee DECIMAL(10, 2) DEFAULT 0.00 COMMENT '额外费用',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠减免金额',
    pay_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实际支付金额',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态：pending-待付款，confirmed-待服务，inProgress-进行中，completed-已完成，cancelled-已取消',
    pay_method TINYINT DEFAULT 0 COMMENT '支付方式：0-余额支付，1-微信支付，2-支付宝',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    transaction_id VARCHAR(64) DEFAULT NULL COMMENT '第三方交易流水号',
    appointment_time VARCHAR(50) DEFAULT NULL COMMENT '预约时间（如：2024-01-20 09:00-12:00）',
    appointment_date DATE DEFAULT NULL COMMENT '预约日期',
    appointment_start_time TIME DEFAULT NULL COMMENT '预约开始时间',
    appointment_end_time TIME DEFAULT NULL COMMENT '预约结束时间',
    service_address VARCHAR(255) DEFAULT NULL COMMENT '服务地址',
    contact_name VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    is_reviewed TINYINT DEFAULT 0 COMMENT '是否已评价：0-否，1-是',
    review_id BIGINT DEFAULT NULL COMMENT '评价ID',
    cancel_reason VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    remark TEXT DEFAULT NULL COMMENT '备注信息',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_service_id (service_id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_pay_status (pay_status),
    KEY idx_create_time (create_time),
    KEY idx_appointment_date (appointment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 4. 订单详情表（用于一个订单包含多个服务项的场景）
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    service_id BIGINT NOT NULL COMMENT '服务项目ID',
    service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
    service_icon VARCHAR(50) DEFAULT NULL COMMENT '服务图标',
    service_image VARCHAR(255) DEFAULT NULL COMMENT '服务图片',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '服务单价',
    quantity INT DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_order_id (order_id),
    KEY idx_order_no (order_no),
    KEY idx_service_id (service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

-- 5. 服务预约表
CREATE TABLE IF NOT EXISTS service_appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    appointment_no VARCHAR(32) NOT NULL COMMENT '预约编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_id BIGINT DEFAULT NULL COMMENT '关联订单ID',
    service_id BIGINT NOT NULL COMMENT '服务项目ID',
    service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    appointment_start_time TIME NOT NULL COMMENT '预约开始时间',
    appointment_end_time TIME NOT NULL COMMENT '预约结束时间',
    appointment_time_str VARCHAR(50) DEFAULT NULL COMMENT '预约时间字符串（如：2024-01-20 09:00-12:00）',
    service_address VARCHAR(255) NOT NULL COMMENT '服务地址',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '预约状态：pending-待确认，confirmed-已确认，inProgress-进行中，completed-已完成，cancelled-已取消',
    confirm_time DATETIME DEFAULT NULL COMMENT '确认时间',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    cancel_reason VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    remark TEXT DEFAULT NULL COMMENT '备注信息',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_appointment_no (appointment_no),
    KEY idx_user_id (user_id),
    KEY idx_order_id (order_id),
    KEY idx_service_id (service_id),
    KEY idx_appointment_date (appointment_date),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务预约表';

-- 6. 订单评价表
CREATE TABLE IF NOT EXISTS order_review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    service_id BIGINT NOT NULL COMMENT '服务项目ID',
    rating TINYINT DEFAULT 5 COMMENT '评分：1-5分',
    content TEXT DEFAULT NULL COMMENT '评价内容',
    images JSON DEFAULT NULL COMMENT '评价图片（JSON数组）',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    reply TEXT DEFAULT NULL COMMENT '商家回复',
    reply_time DATETIME DEFAULT NULL COMMENT '回复时间',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-正常，0-禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_order_id (order_id),
    KEY idx_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_service_id (service_id),
    KEY idx_rating (rating),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单评价表';

-- 7. 余额变动记录表
CREATE TABLE IF NOT EXISTS balance_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    transaction_no VARCHAR(32) NOT NULL COMMENT '交易流水号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type TINYINT NOT NULL COMMENT '交易类型：1-充值，2-消费，3-退款，4-提现，5-系统调整',
    amount DECIMAL(10, 2) NOT NULL COMMENT '交易金额',
    balance_before DECIMAL(10, 2) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(10, 2) NOT NULL COMMENT '交易后余额',
    related_id BIGINT DEFAULT NULL COMMENT '关联业务ID（订单ID、充值ID等）',
    related_type VARCHAR(20) DEFAULT NULL COMMENT '关联业务类型：order-订单，recharge-充值，refund-退款',
    description VARCHAR(255) DEFAULT NULL COMMENT '交易描述',
    record_status TINYINT DEFAULT 1 COMMENT '记录状态：1-成功，0-失败',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_user_id (user_id),
    KEY idx_type (type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='余额变动记录表';
