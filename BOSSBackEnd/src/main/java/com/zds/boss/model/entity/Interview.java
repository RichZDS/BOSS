package com.zds.boss.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "interview")
@Data
public class Interview implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long bossId;
    private Long userId;
    @TableField("interview_time")
    private Date interviewTime;
    private Integer mode;
    private String location;
    private Integer status;
    private String note;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

