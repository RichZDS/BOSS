package com.zds.boss.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 脱敏用户
 *
 * @TableName user
 */
@Data
public class LoginUserVO {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 公司ID（仅Boss角色有效）
     */
    private Long companyId;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
