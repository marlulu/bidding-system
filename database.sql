/*
 内部招标网站系统数据库脚本 (最终交付版)
 包含清晰的初始测试账号及完整的业务字段。
*/

CREATE DATABASE IF NOT EXISTS `bidding_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `bidding_system`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表 (sys_user)
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(MD5加密)',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) NOT NULL COMMENT '角色: ADMIN, SUPPLIER',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '关联供应商ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始测试账号 (密码均为: admin123)
-- MD5("admin123") = 0192023a7bbd73250516f069df18b500
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `avatar`) VALUES 
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'ADMIN', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin'),
('supplier', '0192023a7bbd73250516f069df18b500', '测试供应商', 'SUPPLIER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=supplier');

-- 2. 供应商表 (supplier)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

INSERT INTO `supplier` (`id`, `company_name`, `company_code`, `industry`, `scale`, `qualification_level`, `contact_name`, `contact_phone`, `contact_email`, `description`) VALUES 
(1, '中科智控技术有限公司', '91310000MA1FL11X01', '信息技术', 'LARGE', '一级资质', '王经理', '13811112222', 'wang@zkzk.com', '专注于企业级 AI 解决方案与自动化控制系统。'),
(2, '建工集团第三工程局', '91310000MA1FL11X02', '建筑工程', 'LARGE', '特级资质', '李工', '13922223333', 'li@jg3j.com', '国家大型建筑骨干企业，承建多项地标性建筑。');

-- 关联供应商账号
UPDATE `sys_user` SET `supplier_id` = 1 WHERE `username` = 'supplier';

-- 3. 招标公告表 (bid_announcement)
DROP TABLE IF EXISTS `bid_announcement`;
CREATE TABLE `bid_announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `announcement_no` varchar(100) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `content` longtext,
  `project_name` varchar(255) DEFAULT NULL,
  `project_budget` decimal(18,2) DEFAULT NULL,
  `region` varchar(100) DEFAULT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `bid_deadline` datetime DEFAULT NULL,
  `is_top` tinyint(1) DEFAULT 0,
  `contact_person` varchar(50) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `attachment_files` text,
  `visibility_level` varchar(20) DEFAULT 'PUBLIC',
  `visible_supplier_ids` text,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `publisher_id` bigint(20) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招标公告表';

INSERT INTO `bid_announcement` (`title`, `announcement_no`, `type`, `content`, `project_name`, `project_budget`, `region`, `industry`, `bid_deadline`, `is_top`, `publish_time`) VALUES 
('2026年度集团总部数据中心升级采购项目', 'BID-2026-001', 'BID', '详细的公告内容...', '数据中心升级', 8500000.00, '上海', '信息技术', '2026-02-15 17:00:00', 1, '2026-01-08 09:00:00'),
('智慧园区二期建筑工程招标公告', 'BID-2026-002', 'BID', '详细的公告内容...', '智慧园区二期', 45000000.00, '北京', '建筑工程', '2026-03-01 10:00:00', 1, '2026-01-07 10:00:00');

-- 4. 政策法规表 (policy_regulation)
DROP TABLE IF EXISTS `policy_regulation`;
CREATE TABLE `policy_regulation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `category` varchar(20) NOT NULL,
  `content` longtext,
  `attachment_files` text,
  `publisher_id` bigint(20) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='政策法规表';

INSERT INTO `policy_regulation` (`title`, `category`, `content`) VALUES 
('中华人民共和国招标投标法（2026修订版）', 'REGULATION', '法律全文内容...'),
('企业内部采购管理制度及廉洁准则', 'POLICY', '制度全文内容...');

-- 5. 系统通知表 (system_notice)
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text,
  `type` varchar(20) DEFAULT 'SYSTEM',
  `target_type` varchar(20) DEFAULT 'ALL',
  `target_supplier_ids` text,
  `publisher_id` bigint(20) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

INSERT INTO `system_notice` (`title`, `content`) VALUES 
('系统维护通知', '系统将于本周六凌晨2:00-4:00进行例行维护。'),
('新版供应商管理办法上线', '请各供应商及时查看最新的管理办法。');

SET FOREIGN_KEY_CHECKS = 1;
