/*
 内部招标网站系统数据库脚本 (修复版)
 修复了初始账号密码加密不匹配的问题
*/

CREATE DATABASE IF NOT EXISTS `bidding_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `bidding_system`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `supplier_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始用户 (密码均为 admin123 的 MD5 加密值: 0192023a7bbd73250516f069df18b500)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `avatar`) VALUES 
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'ADMIN', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin'),
('supplier_tech', '0192023a7bbd73250516f069df18b500', '技术部经理', 'SUPPLIER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=tech'),
('supplier_const', '0192023a7bbd73250516f069df18b500', '工程部总监', 'SUPPLIER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=const');

-- 2. 供应商表
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) NOT NULL,
  `company_code` varchar(100) NOT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `scale` varchar(20) DEFAULT NULL,
  `qualification_level` varchar(50) DEFAULT NULL,
  `legal_person` varchar(50) DEFAULT NULL,
  `contact_name` varchar(50) NOT NULL,
  `contact_phone` varchar(20) NOT NULL,
  `contact_email` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `description` text,
  `qualification_files` text,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `supplier` (`company_name`, `company_code`, `industry`, `scale`, `qualification_level`, `contact_name`, `contact_phone`, `contact_email`, `description`) VALUES 
('中科智控技术有限公司', '91310000MA1FL11X01', '信息技术', 'LARGE', '一级资质', '王经理', '13811112222', 'wang@zkzk.com', '专注于企业级 AI 解决方案与自动化控制系统。'),
('建工集团第三工程局', '91310000MA1FL11X02', '建筑工程', 'LARGE', '特级资质', '李工', '13922223333', 'li@jg3j.com', '国家大型建筑骨干企业，承建多项地标性建筑。');

-- 3. 招标公告表
DROP TABLE IF EXISTS `bid_announcement`;
CREATE TABLE `bid_announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `announcement_no` varchar(100) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  `project_budget` decimal(18,2) DEFAULT NULL,
  `region` varchar(100) DEFAULT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `bid_deadline` datetime DEFAULT NULL,
  `is_top` tinyint(1) DEFAULT 0,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `bid_announcement` (`title`, `announcement_no`, `type`, `project_name`, `project_budget`, `region`, `industry`, `bid_deadline`, `is_top`, `publish_time`) VALUES 
('2026年度集团总部数据中心升级采购项目', 'BID-2026-001', 'BID', '数据中心升级', 8500000.00, '上海', '信息技术', '2026-02-15 17:00:00', 1, '2026-01-08 09:00:00'),
('智慧园区二期建筑工程招标公告', 'BID-2026-002', 'BID', '智慧园区二期', 45000000.00, '北京', '建筑工程', '2026-03-01 10:00:00', 1, '2026-01-07 10:00:00');

-- 4. 政策法规表
DROP TABLE IF EXISTS `policy_regulation`;
CREATE TABLE `policy_regulation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `category` varchar(20) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `policy_regulation` (`title`, `category`) VALUES 
('中华人民共和国招标投标法（2026修订版）', 'REGULATION'),
('企业内部采购管理制度及廉洁准则', 'POLICY');

-- 5. 系统通知表
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text,
  `type` varchar(20) DEFAULT 'SYSTEM',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
