package com.zds.boss.model.dto.job;

import lombok.Data;
import java.io.Serializable;

@Data
public class JobPostingUpdateRequest implements Serializable {
    private Long id;
    private String title;
    private String location;
    private String jobType;
    private Integer salaryMin;
    private Integer salaryMax;
    private String description;
    private String requirement;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

