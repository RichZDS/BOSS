package com.zds.boss.aop;

import com.zds.boss.annotation.AuthCheck;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.service.UserService;
import com.zds.boss.utils.UserUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 权限校验 AOP
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 必须有该权限才通过
        if (mustRole != null && !mustRole.isEmpty()) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 状态：0禁用 1正常
            Integer status = loginUser.getStatus();
            // 如果被封号，直接拒绝
            if (status != null && status == 0) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            
            String userRole = loginUser.getUserRole();
            
            // 如果是管理员，直接放行
            if (UserRoleEnum.ADMIN.getValue().equals(userRole)) {
                return joinPoint.proceed();
            }

            // 如果需要管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!UserRoleEnum.ADMIN.getValue().equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
            
            // 如果需要 Boss 权限
            if (UserRoleEnum.BOSS.equals(mustUserRoleEnum)) {
                 if (!UserRoleEnum.BOSS.getValue().equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}
