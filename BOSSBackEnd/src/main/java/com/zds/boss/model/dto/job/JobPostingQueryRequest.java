package com.zds.boss.model.dto.job;

import com.zds.boss.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobPostingQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private Long bossId;
    private Long companyId;
    private String title;
    private String location;
    private String jobType;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

