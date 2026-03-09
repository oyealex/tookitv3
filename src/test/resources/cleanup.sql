-- 清理所有设备记录
DELETE FROM device;
-- 验证清理结果
SELECT COUNT(*) FROM device;
