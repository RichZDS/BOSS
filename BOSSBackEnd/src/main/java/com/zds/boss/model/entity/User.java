package com.zds.boss.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 候选人用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录名（唯一）
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 密码哈希
     */
    @TableField("password")
    private String userPassword;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    @TableField("username")
    private String userName;

    /**
     * 用户角色：user-求职者, boss-Boss, admin-管理员
     */
    @TableField("user_role")
    private String userRole;

    /**
     * 公司ID（仅Boss角色有效）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 状态：0禁用 1正常
     */
    private Integer status;


    /**
     * 
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 
     */
    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 软删：0否 1是
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDelete;

    /**
     * 
     */
    @TableField("deleted_at")
    private Date deletedAt;

    /**
     * 简介
     */
    @TableField("profile")
    private String userProfile;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
