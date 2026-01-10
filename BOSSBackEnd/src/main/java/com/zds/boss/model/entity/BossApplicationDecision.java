package com.zds.boss.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "boss_application_decision")
@Data
public class BossApplicationDecision implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long bossId;
    private Integer decision;
    private Integer stage;
    private String note;
    @TableField("decided_at")
    private Date decidedAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

