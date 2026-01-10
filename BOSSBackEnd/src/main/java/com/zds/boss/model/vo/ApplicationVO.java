package com.zds.boss.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ApplicationVO implements Serializable {
    private Long id;
    private Long userId;
    private Long resumeId;
    private Long jobId;
    private Long bossId;
    private Integer status;
    private Date appliedAt;
    private Date updatedAt;
    private static final long serialVersionUID = 1L;
}

