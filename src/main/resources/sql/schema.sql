-- PRAGMA设置
PRAGMA journal_mode=WAL;

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    ip VARCHAR(15) PRIMARY KEY,
    name VARCHAR(120),
    type VARCHAR(20) NOT NULL,
    model VARCHAR(100),
    version VARCHAR(50),
    username VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 设备表索引
CREATE INDEX IF NOT EXISTS idx_device_name ON device(name);
CREATE INDEX IF NOT EXISTS idx_device_type ON device(type);
CREATE INDEX IF NOT EXISTS idx_device_model ON device(model);
CREATE INDEX IF NOT EXISTS idx_device_version ON device(version);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operation_type VARCHAR(20) NOT NULL,
    object_type VARCHAR(50) NOT NULL,
    object_id VARCHAR(255) NOT NULL,
    object_name VARCHAR(255),
    object_extra TEXT,
    description VARCHAR(500),
    result VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(500),
    operator VARCHAR(100),
    operator_ip VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志表索引
CREATE INDEX IF NOT EXISTS idx_operation_log_time ON operation_log(operation_time DESC);
CREATE INDEX IF NOT EXISTS idx_operation_log_object ON operation_log(object_type, object_id);
CREATE INDEX IF NOT EXISTS idx_operation_log_result ON operation_log(result);
CREATE INDEX IF NOT EXISTS idx_operation_log_name ON operation_log(object_name);