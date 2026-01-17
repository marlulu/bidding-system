package com.bidding.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExpertVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String specialty;
    private String title;
    private String phone;
    private String email;
    private String description;
    private java.time.LocalDateTime createTime;
}
