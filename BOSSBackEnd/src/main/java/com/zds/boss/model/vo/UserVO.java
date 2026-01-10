package com.zds.boss.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 脱敏用户
 *
 * @TableName user
 */
@Data
public class UserVO {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 状态：0禁用 1正常
     */
    private Integer status;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 公司ID（仅Boss角色有效）
     */
    private Long companyId;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
