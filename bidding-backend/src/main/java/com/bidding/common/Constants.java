package com.bidding.common;

public class Constants {
    // JWT相关
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    
    // 用户角色
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPPLIER = "SUPPLIER";
    
    // 用户状态
    public static final Integer USER_STATUS_DISABLED = 0;
    public static final Integer USER_STATUS_ENABLED = 1;
    public static final Integer USER_STATUS_PENDING_REVIEW = 2; // 用户待审核状态
    
    // 供应商状态
    public static final Integer SUPPLIER_STATUS_PENDING = 0;
    public static final Integer SUPPLIER_STATUS_APPROVED = 1;
    public static final Integer SUPPLIER_STATUS_REJECTED = 2;
    
    // 公告类型
    public static final String ANNOUNCEMENT_TYPE_BID = "BID";
    public static final String ANNOUNCEMENT_TYPE_CHANGE = "CHANGE";
    public static final String ANNOUNCEMENT_TYPE_TERMINATE = "TERMINATE";
    public static final String ANNOUNCEMENT_TYPE_RESULT = "RESULT";
    
    // 公告状态
    public static final String ANNOUNCEMENT_STATUS_DRAFT = "DRAFT";
    public static final String ANNOUNCEMENT_STATUS_PUBLISHED = "PUBLISHED";
    public static final String ANNOUNCEMENT_STATUS_CLOSED = "CLOSED";
    
    // 可见级别
    public static final String VISIBILITY_PUBLIC = "PUBLIC";
    public static final String VISIBILITY_RESTRICTED = "RESTRICTED";
    
    // 政策法规分类
    public static final String POLICY_CATEGORY_POLICY = "POLICY";
    public static final String POLICY_CATEGORY_REGULATION = "REGULATION";
    public static final String POLICY_CATEGORY_GUIDE = "GUIDE";
    
    // 通知类型
    public static final String NOTICE_TYPE_SYSTEM = "SYSTEM";
    public static final String NOTICE_TYPE_POLICY = "POLICY";
    public static final String NOTICE_TYPE_ANNOUNCEMENT = "ANNOUNCEMENT";
    
    // 通知目标类型
    public static final String NOTICE_TARGET_ALL = "ALL";
    public static final String NOTICE_TARGET_SUPPLIER = "SUPPLIER";
}
