package com.zds.boss.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class CompanyVO implements Serializable {
    private Long id;
    private String name;
    private String industry;
    private String sizeRange;
    private String website;
    private String address;
    private String intro;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
    private static final long serialVersionUID = 1L;
}

