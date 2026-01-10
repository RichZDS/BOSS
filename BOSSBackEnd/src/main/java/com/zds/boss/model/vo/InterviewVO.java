package com.zds.boss.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class InterviewVO implements Serializable {
    private Long id;
    private Long applicationId;
    private Long bossId;
    private Long userId;
    private Date interviewTime;
    private Integer mode;
    private String location;
    private Integer status;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    private static final long serialVersionUID = 1L;
}

