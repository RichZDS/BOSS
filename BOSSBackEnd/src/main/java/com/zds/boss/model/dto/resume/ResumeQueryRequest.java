package com.zds.boss.model.dto.resume;

import com.zds.boss.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询简历请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumeQueryRequest extends PageRequest implements Serializable {

    /**
     * 简历ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 简历标题
     */
    private String resumeTitle;

    /**
     * 是否默认：0否 1是
     */
    private Integer isDefault;

    private static final long serialVersionUID = 1L;
}
