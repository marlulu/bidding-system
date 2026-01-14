package com.bidding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_expert_extraction")
public class ExpertExtraction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 招标公告ID
     */
    private Long announcementId;

    /**
     * 专家ID
     */
    private Long expertId;

    /**
     * 抽取时间
     */
    private LocalDateTime extractTime;

    /**
     * 抽取人ID
     */
    private Long extractorId;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer deleted;
}
