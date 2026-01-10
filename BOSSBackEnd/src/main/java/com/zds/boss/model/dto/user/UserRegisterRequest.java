package com.zds.boss.model.dto.user;/*
 *@auther 郑笃实
 *@version 1.0
 *
 */

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1999618084091901791L;
    private String usrAccount;
    private String userPassword;
    private String checkPassword;
    private String userRole;
}
