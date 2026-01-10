package com.zds.boss.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class JobPostingVO implements Serializable {
    private Long id;
    private Long bossId;
    private Long companyId;
    private String title;
    private String location;
    private String jobType;
    private Integer salaryMin;
    private Integer salaryMax;
    private String description;
    private String requirement;
    private Integer status;
    private Date publishAt;
    private Date createdAt;
    private Date updatedAt;
    private static final long serialVersionUID = 1L;
}

