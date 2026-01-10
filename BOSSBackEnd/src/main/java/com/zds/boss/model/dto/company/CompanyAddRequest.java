package com.zds.boss.model.dto.company;

import lombok.Data;
import java.io.Serializable;

@Data
public class CompanyAddRequest implements Serializable {
    private String name;
    private String industry;
    private String sizeRange;
    private String website;
    private String address;
    private String intro;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

