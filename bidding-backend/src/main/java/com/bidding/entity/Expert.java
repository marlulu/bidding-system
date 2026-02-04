package com.bidding.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_expert")
public class Expert implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 专家姓名
     */
    private String name;

    /**
     * 专业领域
     */
    private String specialty;

    /**
     * 职称
     */
    private String title;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号/专家证号
     */
    private String idCardNumber;

    /**
     * 专家证书照片
     */
    private String certificatePhoto;

    /**
     * 专家简介
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer deleted;
}
