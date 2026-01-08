package com.bidding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bid_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String announcementNo;
    
    private String type;
    
    private String content;
    
    private String projectName;
    
    private BigDecimal projectBudget;
    
    private LocalDateTime bidDeadline;
    
    private String contactPerson;
    
    private String contactPhone;
    
    private String attachmentFiles;
    
    private String visibilityLevel;
    
    private String visibleSupplierIds;
    
    private String status;
    
    private Long publisherId;
    
    private LocalDateTime publishTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
