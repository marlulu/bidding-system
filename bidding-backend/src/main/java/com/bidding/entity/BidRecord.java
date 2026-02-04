package com.bidding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bid_record")
public class BidRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long announcementId;
    private Long supplierId;
    private BigDecimal bidAmount;
    private String status; // SUBMITTED, WIN, LOSE
    private LocalDateTime createTime;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String announcementTitle;
}
