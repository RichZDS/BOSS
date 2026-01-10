package com.zds.boss.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简历表
 * @TableName resume
 */
@TableName(value ="resume")
@Data
public class Resume implements Serializable {
    /**
     * 简历ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（无外键）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 简历标题
     */
    @TableField("resume_title")
    private String resumeTitle;

    /**
     * 是否默认：0否 1是
     */
    @TableField("is_default")
    private Integer isDefault;

    /**
     * 摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 正文（可存Markdown/JSON/HTML）
     */
    @TableField("content")
    private String content;

    /**
     * 附件URL（PDF/Word）
     */
    @TableField("attachment_url")
    private String attachmentUrl;

    /**
     * 
     */
    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
