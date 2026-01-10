package com.zds.boss.model.dto.interview;

import com.zds.boss.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class InterviewQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private Long applicationId;
    private Long bossId;
    private Long userId;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

