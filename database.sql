-- 内部招标网站系统数据库 (丰富样例数据版)
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `supplier_id` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username`(`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `avatar`) VALUES 
('admin', '7a57a5a743894a0e', '系统管理员', 'ADMIN', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin'),
('supplier_tech', '7a57a5a743894a0e', '技术部经理', 'SUPPLIER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=tech'),
('supplier_const', '7a57a5a743894a0e', '工程部总监', 'SUPPLIER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=const');

-- 2. 供应商表
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) NOT NULL,
  `company_code` varchar(100) NOT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `scale` varchar(20) DEFAULT NULL,
  `qualification_level` varchar(50) DEFAULT NULL,
  `legal_person` varchar(50) DEFAULT NULL,
  `contact_name` varchar(50) NOT NULL,
  `contact_phone` varchar(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `supplier` (`company_name`, `company_code`, `industry`, `scale`, `qualification_level`, `contact_name`, `contact_phone`, `description`) VALUES 
('中科智控技术有限公司', '91310000MA1FL11X01', '信息技术', 'LARGE', '一级资质', '王经理', '13811112222', '专注于企业级 AI 解决方案与自动化控制系统。'),
('建工集团第三工程局', '91310000MA1FL11X02', '建筑工程', 'LARGE', '特级资质', '李工', '13922223333', '国家大型建筑骨干企业，承建多项地标性建筑。'),
('华东医疗器械有限公司', '91310000MA1FL11X03', '医疗器械', 'MEDIUM', '二级资质', '陈主任', '13733334444', '专业生产高端影像设备与实验室分析仪器。'),
('优选办公用品商贸', '91310000MA1FL11X04', '办公用品', 'SMALL', '三级资质', '张经理', '13644445555', '一站式办公物资供应服务商，覆盖全国配送。'),
('德勤咨询（中国）', '91310000MA1FL11X05', '咨询服务', 'LARGE', '一级资质', '赵顾问', '13555556666', '全球领先的专业咨询机构，提供战略与数字化转型建议。'),
('顺风物流自动化', '91310000MA1FL11X06', '物流运输', 'MEDIUM', '二级资质', '孙经理', '13466667777', '智能仓储与无人配送技术领跑者。');

-- 3. 招标公告表
DROP TABLE IF EXISTS `bid_announcement`;
CREATE TABLE `bid_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `announcement_no` varchar(100) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  `project_budget` decimal(18, 2) DEFAULT NULL,
  `region` varchar(100) DEFAULT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `bid_deadline` datetime DEFAULT NULL,
  `is_top` tinyint(1) DEFAULT 0,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `bid_announcement` (`title`, `announcement_no`, `type`, `project_name`, `project_budget`, `region`, `industry`, `bid_deadline`, `is_top`, `publish_time`) VALUES 
('2026年度集团总部数据中心升级采购项目', 'BID-2026-001', 'BID', '数据中心升级', 8500000.00, '上海', '信息技术', '2026-02-15 17:00:00', 1, NOW()),
('智慧园区二期建筑工程招标公告', 'BID-2026-002', 'BID', '智慧园区二期', 45000000.00, '北京', '建筑工程', '2026-03-01 10:00:00', 1, NOW()),
('关于办公耗材集中采购项目的变更公告', 'CHG-2026-001', 'CHANGE', '办公耗材采购', 1200000.00, '杭州', '办公用品', '2026-01-25 14:00:00', 0, NOW()),
('医疗影像设备维保服务中标结果公示', 'RES-2026-001', 'RESULT', '影像设备维保', 3000000.00, '广州', '医疗器械', NULL, 0, NOW()),
('企业数字化转型战略咨询服务招标', 'BID-2026-003', 'BID', '数字化转型咨询', 2000000.00, '深圳', '咨询服务', '2026-02-10 16:00:00', 0, NOW()),
('冷链物流自动化分拣系统采购', 'BID-2026-004', 'BID', '冷链分拣系统', 12000000.00, '成都', '物流运输', '2026-03-15 09:00:00', 0, NOW());

-- 4. 政策法规表
DROP TABLE IF EXISTS `policy_regulation`;
CREATE TABLE `policy_regulation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `category` varchar(20) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `policy_regulation` (`title`, `category`) VALUES 
('中华人民共和国招标投标法（2026修订版）', 'REGULATION'),
('企业内部采购管理制度及廉洁准则', 'POLICY'),
('供应商入驻资质审核标准指南', 'GUIDE'),
('电子招投标平台操作手册 v3.0', 'GUIDE');

SET FOREIGN_KEY_CHECKS = 1;
