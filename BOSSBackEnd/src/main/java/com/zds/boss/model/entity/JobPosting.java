package com.zds.boss.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "job_posting")
@Data
public class JobPosting implements Serializable {
    @TableId(type = IdType.AUTO)
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
    @TableField("publish_at")
    private Date publishAt;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

