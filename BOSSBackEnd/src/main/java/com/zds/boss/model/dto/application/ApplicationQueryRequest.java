package com.zds.boss.model.dto.application;

import com.zds.boss.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private Long userId;
    private Long resumeId;
    private Long jobId;
    private Long bossId;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

