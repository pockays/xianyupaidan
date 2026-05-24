CREATE DATABASE IF NOT EXISTS xianyupaidan
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE xianyupaidan;

-- 超级管理员 (pre-configured)
CREATE TABLE IF NOT EXISTS super_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 管理员 (one per tenant)
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    xianyu_id VARCHAR(100),
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 闲鱼用户
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) NOT NULL,
    xianyu_id VARCHAR(100),
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB;

-- 排单
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    email VARCHAR(100),
    status VARCHAR(20) DEFAULT 'WAITING',
    total_price DECIMAL(10,2) DEFAULT 0.00,
    submitted TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- 排单分类
CREATE TABLE IF NOT EXISTS order_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    category_name VARCHAR(50),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB;

-- 排单项
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    link_url VARCHAR(500),
    note VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'PENDING',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB;

-- 预设标签
CREATE TABLE IF NOT EXISTS preset_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) NOT NULL,
    name VARCHAR(50) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB;

-- 系统配置
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) NOT NULL UNIQUE,
    order_enabled TINYINT DEFAULT 1,
    announcement TEXT
) ENGINE=InnoDB;

-- 审计日志 (CDC audit log populated by Canal + Kafka consumer)
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(64) NOT NULL,
    operation_type VARCHAR(16) NOT NULL,
    record_id VARCHAR(64),
    old_data JSON,
    new_data JSON,
    operator VARCHAR(64),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_table_name (table_name),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at),
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Canal CDC 用户
CREATE USER IF NOT EXISTS 'canal'@'%' IDENTIFIED BY 'canal';
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
FLUSH PRIVILEGES;

-- 初始化超级管理员 (密码: 134511130qq)
INSERT INTO super_admin (username, password_hash) VALUES
('admin', '$2b$10$72kuQTuQVv3dxtmTuoMfYOx0iNhXesXIX6xIbjMkqKFyIMCJxzd7W')
ON DUPLICATE KEY UPDATE username = username;

-- 初始化默认管理员 (密码: 134511130qq)
INSERT INTO admin (tenant_id, username, password_hash, email, status) VALUES
('default', '咩卡布', '$2b$10$72kuQTuQVv3dxtmTuoMfYOx0iNhXesXIX6xIbjMkqKFyIMCJxzd7W', 'manager@qq.com', 1)
ON DUPLICATE KEY UPDATE username = username;

-- 初始化默认标签
INSERT INTO preset_tag (tenant_id, name, sort_order) VALUES
('default', '衣服', 1),
('default', '头发', 2),
('default', '插件', 3),
('default', '饰品', 4),
('default', '妆容', 5),
('default', '表情动作', 6);

-- 初始化系统配置
INSERT INTO system_config (tenant_id, order_enabled, announcement) VALUES
('default', 1, '')
ON DUPLICATE KEY UPDATE tenant_id = tenant_id;
