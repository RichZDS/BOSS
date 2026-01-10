package com.zds.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.mapper.UserMapper;
import com.zds.boss.model.dto.user.UserQueryRequest;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.LoginUserVO;
import com.zds.boss.model.vo.UserVO;
import com.zds.boss.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 33882
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-09-21 19:45:22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userRole) {
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userAccount.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过长");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (userPassword.length() > 15) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        
        // 角色处理
        String finalRole = UserRoleEnum.USER.getValue();
        if (StrUtil.isNotBlank(userRole)) {
            if ("1".equals(userRole)) {
                finalRole = UserRoleEnum.USER.getValue();
            } else if ("2".equals(userRole)) {
                finalRole = UserRoleEnum.BOSS.getValue();
            } else if ("3".equals(userRole)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持注册管理员");
            } else {
                 UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(userRole);
                 if (roleEnum != null) {
                     if (UserRoleEnum.ADMIN.equals(roleEnum)) {
                         throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持注册管理员");
                     }
                     finalRole = roleEnum.getValue();
                 }
            }
        }

        //校验账号相同
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        //加密
        String pas = getEncryptPassword(userPassword);

        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(pas);
        user.setUserName(userAccount); // 默认昵称
        user.setUserRole(finalRole);
        
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败,数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号错误");
        }
        if (userPassword.length() < 8 || userPassword.length() > 15) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码错误");
        }

        //校验密码是否正确
        String pas = getEncryptPassword(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("password", pas);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        //保存用户登录态信息
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return getLoginUserVO(user);
    }

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        // 数据库无角色字段，默认为 USER
        if (loginUserVO.getUserRole() == null) {
            loginUserVO.setUserRole(UserRoleEnum.USER.getValue());
        }
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        // 数据库无角色字段，默认为 USER
        if (userVO.getUserRole() == null) {
            userVO.setUserRole(UserRoleEnum.USER.getValue());
        }
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 密码加密
     *
     * @param userPassword 用户输入的原始密码
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "zhengdushi";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        // 从会话中获取登录用户对象
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 检查用户对象或用户ID是否为空
        if (currentUser == null || currentUser.getId() == null) {
            // 如果未登录，抛出业务异常
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        // 获取用户ID
        long userId = currentUser.getId();
        // 根据ID从数据库查询用户信息
        currentUser = this.getById(userId);
        // 检查数据库中是否存在该用户
        if (currentUser == null) {
            // 如果用户不存在，抛出业务异常
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 用户登出方法
     *
     * @param request HttpServletRequest对象，用于获取会话信息
     * @return 登出成功返回true
     * @throws BusinessException 当用户未登录时抛出业务异常
     */
    @Override
    public boolean userLogOut(HttpServletRequest request) {
        // 从会话中获取用户登录状态信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        // 检查用户是否已登录
        if (userObj == null) {
            // 如果用户未登录，抛出业务异常
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态，使用户登出
        request.getSession().invalidate();
        // 返回登出成功标志
        return true;
    }

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "user_account", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "username", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "profile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return queryWrapper;
    }


}
