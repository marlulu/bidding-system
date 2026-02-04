package com.bidding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String region;
    private String industry;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime bidDeadline;
    private Integer isTop;
    private String contactPerson;
    private String contactPhone;
    private String attachmentFiles;
    private String visibilityLevel;
    private String visibleSupplierIds;
    private String status;
    private Long publisherId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
