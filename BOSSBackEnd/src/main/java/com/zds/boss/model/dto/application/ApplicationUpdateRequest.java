package com.zds.boss.model.dto.application;

import lombok.Data;
import java.io.Serializable;

@Data
public class ApplicationUpdateRequest implements Serializable {
    private Long id;
    private Long resumeId;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

