package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.user.UserQueryRequest;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.vo.LoginUserVO;
import com.zds.boss.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author 33882
 * @description 针对表【user(候选人用户表)】的数据库操作Service
 * @createDate 2025-12-30 23:14:37
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册方法
     *
     * @param userAccount   用户账号，用于唯一标识一个用户
     * @param userPassword  用户密码，需要进行加密处理
     * @param checkPassword 确认密码，用于二次验证用户输入的密码是否正确
     * @param userRole      用户角色
     * @return 返回一个长整型值，通常用于表示操作结果：
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userRole);

    /**
     * 用户登录方法
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request HTTP请求对象
     * @return 返回一个用户对象，通常用于表示登录成功后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户信息
     *
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户VO
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户VO列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 密码加密
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    boolean userLogOut(HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
