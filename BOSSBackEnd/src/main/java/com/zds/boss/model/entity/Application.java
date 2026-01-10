package com.zds.boss.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "application")
@Data
public class Application implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long resumeId;
    private Long jobId;
    private Long bossId;
    private Integer status;
    @TableField("applied_at")
    private Date appliedAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

