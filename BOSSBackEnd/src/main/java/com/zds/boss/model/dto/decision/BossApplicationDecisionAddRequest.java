package com.zds.boss.model.dto.decision;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class BossApplicationDecisionAddRequest implements Serializable {
    private Long applicationId;
    private Long bossId;
    private Integer decision;
    private Integer stage;
    private String note;
    private Date decidedAt;
    private static final long serialVersionUID = 1L;
}

