package com.zds.boss.model.dto.application;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApplicationAddRequest implements Serializable {
    private Long resumeId;
    private Long jobId;
    private Long bossId;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

