package com.zds.boss.model.dto.interview;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class InterviewAddRequest implements Serializable {
    private Long applicationId;
    private Long bossId;
    private Long userId;
    private Date interviewTime;
    private Integer mode;
    private String location;
    private Integer status;
    private String note;
    private static final long serialVersionUID = 1L;
}

