package com.zds.boss.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class BossApplicationDecisionVO implements Serializable {
    private Long id;
    private Long applicationId;
    private Long bossId;
    private Integer decision;
    private Integer stage;
    private String note;
    private Date decidedAt;
    private Date updatedAt;
    private static final long serialVersionUID = 1L;
}

