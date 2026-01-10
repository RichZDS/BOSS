package com.zds.boss.model.dto.interview;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class InterviewUpdateRequest implements Serializable {
    private Long id;
    private Date interviewTime;
    private Integer mode;
    private String location;
    private Integer status;
    private String note;
    private static final long serialVersionUID = 1L;
}

