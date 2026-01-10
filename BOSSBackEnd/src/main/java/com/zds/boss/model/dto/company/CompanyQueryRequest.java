package com.zds.boss.model.dto.company;

import com.zds.boss.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private String name;
    private String industry;
    private Integer status;
    private static final long serialVersionUID = 1L;
}

