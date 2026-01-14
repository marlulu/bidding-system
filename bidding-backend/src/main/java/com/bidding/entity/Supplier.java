package com.bidding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("supplier")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyName;
    private String companyCode;
    private String legalPerson;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String industry;
    private String scale;
    private String qualificationLevel;
    private String description;
    private String qualificationFiles;
        private Integer status; // 0-待审核, 1-审核通过, 2-审核不通过

    private String auditRemark; // 审核意见

    private Long auditorId; // 审核人ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
