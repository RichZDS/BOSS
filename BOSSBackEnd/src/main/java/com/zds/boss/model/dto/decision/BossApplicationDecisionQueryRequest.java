package com.zds.boss.model.dto.decision;

import com.zds.boss.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class BossApplicationDecisionQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private Long applicationId;
    private Long bossId;
    private Integer decision;
    private Integer stage;
    private static final long serialVersionUID = 1L;
}

