package com.zds.boss.model.dto.decision;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class BossApplicationDecisionUpdateRequest implements Serializable {
    private Long id;
    private Integer decision;
    private Integer stage;
    private String note;
    private Date decidedAt;
    private static final long serialVersionUID = 1L;
}

