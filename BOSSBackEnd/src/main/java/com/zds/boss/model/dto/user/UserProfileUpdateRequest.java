package com.zds.boss.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileUpdateRequest implements Serializable {

    private Long id;

    private String oldPassword;

    private String userPassword;

    private String email;

    private String phone;

    private String userProfile;

    private static final long serialVersionUID = 1L;
}

