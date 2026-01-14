package com.zds.boss.model.dto.resume;

import lombok.Data;

import java.io.Serializable;

/**
 * AI简历优化请求
 */
@Data
public class ResumeAiOptimizeRequest implements Serializable {

    /**
     * 简历标题
     */
    private String resumeTitle;

    /**
     * 个人摘要
     */
    private String summary;

    /**
     * 详细内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
