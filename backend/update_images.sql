-- 数据库：dev_gsb_1
-- 更新图片URL脚本（用于修复已入库的数据）
-- 说明：此脚本将轮播图和平台简介的图片URL更新为使用Unsplash主题相关稳定图片

USE dev_gsb_1;

-- 更新轮播图图片URL
-- 家政保洁服务（清洁、打扫、整洁的家）
UPDATE carousel 
SET image_url = 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800&h=500&fit=crop&auto=format' 
WHERE title = '家政保洁服务' 
AND deleted = 0;

-- 家电维修服务（维修工人、工具、家电维修）
UPDATE carousel 
SET image_url = 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800&h=500&fit=crop&auto=format' 
WHERE title = '家电维修服务' 
AND deleted = 0;

-- 教育培训服务（学习、教室、学生、老师）
UPDATE carousel 
SET image_url = 'https://images.unsplash.com/photo-1509062522246-3755977927d7?w=800&h=500&fit=crop&auto=format' 
WHERE title = '教育培训服务' 
AND deleted = 0;

-- 健康医疗服务（医院、医生、健康、医疗）
UPDATE carousel 
SET image_url = 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=800&h=500&fit=crop&auto=format' 
WHERE title = '健康医疗服务' 
AND deleted = 0;

-- 更新平台简介图片URL（社区、邻里、城市生活）
UPDATE platform_intro 
SET image_url = 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=800&h=500&fit=crop&auto=format' 
WHERE title = '平台简介' 
AND deleted = 0;

-- 显示更新结果
SELECT '轮播图更新结果:' AS message;
SELECT id, title, image_url FROM carousel WHERE deleted = 0 ORDER BY sort_order;

SELECT '平台简介更新结果:' AS message;
SELECT id, title, image_url FROM platform_intro WHERE deleted = 0;
