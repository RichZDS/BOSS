package com.zds.boss.model.dto.resume;

import lombok.Data;
import java.io.Serializable;

/**
 * 更新简历请求
 */
@Data
public class ResumeUpdateRequest implements Serializable {

    /**
     * 简历ID
     */
    private Long id;

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
     * 正文（可存Markdown/JSON/HTML）
     */
    private String content;

    /**
     * 附件URL（PDF/Word）
     */
    private String attachmentUrl;

    private static final long serialVersionUID = 1L;
}
