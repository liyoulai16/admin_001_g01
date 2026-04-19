-- 数据库：dev_gsb_1
-- 更新图片URL脚本（用于修复已入库的数据）
-- 说明：此脚本将轮播图和平台简介的图片URL更新为使用picsum.photos稳定图片服务

USE dev_gsb_1;

-- 更新轮播图图片URL
-- 家政保洁服务
UPDATE carousel 
SET image_url = 'https://picsum.photos/seed/home-cleaning/800/500' 
WHERE title = '家政保洁服务' 
AND deleted = 0;

-- 家电维修服务
UPDATE carousel 
SET image_url = 'https://picsum.photos/seed/appliance-repair/800/500' 
WHERE title = '家电维修服务' 
AND deleted = 0;

-- 教育培训服务
UPDATE carousel 
SET image_url = 'https://picsum.photos/seed/education/800/500' 
WHERE title = '教育培训服务' 
AND deleted = 0;

-- 健康医疗服务
UPDATE carousel 
SET image_url = 'https://picsum.photos/seed/healthcare/800/500' 
WHERE title = '健康医疗服务' 
AND deleted = 0;

-- 更新平台简介图片URL
UPDATE platform_intro 
SET image_url = 'https://picsum.photos/seed/community/800/500' 
WHERE title = '平台简介' 
AND deleted = 0;

-- 显示更新结果
SELECT '轮播图更新结果:' AS message;
SELECT id, title, image_url FROM carousel WHERE deleted = 0 ORDER BY sort_order;

SELECT '平台简介更新结果:' AS message;
SELECT id, title, image_url FROM platform_intro WHERE deleted = 0;
