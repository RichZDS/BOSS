package com.zds.boss.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 简历视图
 */
@Data
public class ResumeVO implements Serializable {
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

    /**
     * 摘要
     */
    private String summary;

    /**
     * 正文
     */
    private String content;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 创建时间
     */
    private Date createdAt;

    private static final long serialVersionUID = 1L;
}
