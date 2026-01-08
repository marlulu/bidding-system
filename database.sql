-- 内部招标网站系统数据库 (升级版)
-- 数据库名: bidding_system

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) NOT NULL COMMENT '角色(ADMIN, SUPPLIER)',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态(0-禁用, 1-启用)',
  `supplier_id` bigint DEFAULT NULL COMMENT '关联供应商ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_username`(`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 2. 供应商表
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) NOT NULL COMMENT '公司名称',
  `company_code` varchar(100) NOT NULL COMMENT '信用代码',
  `industry` varchar(100) DEFAULT NULL COMMENT '行业',
  `scale` varchar(20) DEFAULT NULL COMMENT '规模',
  `qualification_level` varchar(50) DEFAULT NULL COMMENT '资质等级',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法人',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `description` text DEFAULT NULL COMMENT '公司介绍',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-待核, 1-已证, 2-拒绝',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_code`(`company_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '供应商表';

-- 3. 招标公告表
DROP TABLE IF EXISTS `bid_announcement`;
CREATE TABLE `bid_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `announcement_no` varchar(100) DEFAULT NULL COMMENT '编号',
  `type` varchar(20) NOT NULL COMMENT 'BID, CHANGE, TERMINATE, RESULT',
  `content` longtext DEFAULT NULL COMMENT '内容',
  `project_name` varchar(255) DEFAULT NULL COMMENT '项目名',
  `project_budget` decimal(18, 2) DEFAULT NULL COMMENT '预算',
  `region` varchar(100) DEFAULT NULL COMMENT '地区',
  `industry` varchar(100) DEFAULT NULL COMMENT '所属行业',
  `bid_deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `is_top` tinyint(1) DEFAULT 0 COMMENT '是否置顶',
  `visibility_level` varchar(20) DEFAULT 'PUBLIC' COMMENT 'PUBLIC, RESTRICTED',
  `visible_supplier_ids` json DEFAULT NULL COMMENT '可见供应商ID',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT 'DRAFT, PUBLISHED, CLOSED',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '招标公告表';

-- 4. 收藏记录表
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_id` bigint NOT NULL COMMENT '目标ID(公告或供应商)',
  `target_type` varchar(20) NOT NULL COMMENT 'ANNOUNCEMENT, SUPPLIER',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_target`(`user_id`, `target_id`, `target_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '收藏表';

-- 5. 投标记录表
DROP TABLE IF EXISTS `bid_record`;
CREATE TABLE `bid_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `announcement_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  `bid_amount` decimal(18, 2) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED, WIN, LOSE',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '投标记录表';

-- 6. 政策法规表
DROP TABLE IF EXISTS `policy_regulation`;
CREATE TABLE `policy_regulation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `category` varchar(20) NOT NULL COMMENT 'POLICY, REGULATION, GUIDE',
  `content` longtext,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '政策法规表';

-- 7. 系统通知表
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text,
  `type` varchar(20) DEFAULT 'SYSTEM',
  `target_type` varchar(20) DEFAULT 'ALL',
  `target_supplier_ids` json DEFAULT NULL,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '通知表';

-- 初始数据
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES ('admin', '7a57a5a743894a0e', '系统管理员', 'ADMIN', 1);
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`, `supplier_id`) VALUES ('supplier', '7a57a5a743894a0e', '演示供应商', 'SUPPLIER', 1, 1);

INSERT INTO `supplier` (`id`, `company_name`, `company_code`, `industry`, `scale`, `qualification_level`, `contact_name`, `contact_phone`, `status`) 
VALUES (1, '先锋科技股份有限公司', '91310000MA1FL11X11', '信息技术', 'LARGE', '一级资质', '张经理', '13800138000', 1);

INSERT INTO `bid_announcement` (`title`, `type`, `project_name`, `project_budget`, `region`, `industry`, `is_top`, `status`, `publish_time`) 
VALUES ('2026年度企业数字化转型软件采购项目', 'BID', '数字化转型项目', 5000000.00, '上海', '信息技术', 1, 'PUBLISHED', NOW());

SET FOREIGN_KEY_CHECKS = 1;
