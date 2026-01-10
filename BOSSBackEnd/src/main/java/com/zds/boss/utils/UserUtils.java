package com.zds.boss.utils;

import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import org.springframework.stereotype.Component;

/**
 * 用户工具类
 */
@Component
public class UserUtils {

    /**
     * 判断用户是否是管理员
     *
     * @param user 用户对象
     * @return true 如果是管理员，否则 false
     */
    public static boolean isAdmin(User user) {
        if (user == null || user.getUserRole() == null) {
            return false;
        }
        return UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }
}
