-- 为 service_order 表添加退款相关字段
-- 数据库：dev_gsb_1

USE dev_gsb_1;

-- 1. 添加退款金额字段
ALTER TABLE service_order 
ADD COLUMN refund_amount DECIMAL(10, 2) DEFAULT 0 COMMENT '退款金额' AFTER cancel_time;

-- 2. 添加退款原因字段
ALTER TABLE service_order 
ADD COLUMN refund_reason VARCHAR(500) COMMENT '退款原因' AFTER refund_amount;

-- 3. 添加退款时间字段
ALTER TABLE service_order 
ADD COLUMN refund_time DATETIME COMMENT '退款时间' AFTER refund_reason;

-- 4. 添加退款状态字段
ALTER TABLE service_order 
ADD COLUMN refund_status VARCHAR(20) COMMENT '退款状态：refunding-退款中，refunded-已退款，rejected-已拒绝' AFTER refund_time;

-- 5. 验证字段是否添加成功
DESCRIBE service_order;
