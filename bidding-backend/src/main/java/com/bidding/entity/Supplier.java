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
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
