package com.zds.boss.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI简历优化响应
 */
@Data
public class ResumeAiOptimizeVO implements Serializable {

    /**
     * 优化后的简历标题
     */
    private String resumeTitle;

    /**
     * 优化后的个人摘要
     */
    private String summary;

    /**
     * 优化后的详细内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
