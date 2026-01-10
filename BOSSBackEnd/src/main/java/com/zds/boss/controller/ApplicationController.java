package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.dto.application.ApplicationAddRequest;
import com.zds.boss.model.dto.application.ApplicationQueryRequest;
import com.zds.boss.model.dto.application.ApplicationUpdateRequest;
import com.zds.boss.model.entity.Application;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.ApplicationVO;
import com.zds.boss.service.ApplicationService;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@Slf4j
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addApplication(@RequestBody ApplicationAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = applicationService.addApplication(addRequest, loginUser);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApplication(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = applicationService.deleteApplication(deleteRequest.getId(), loginUser);
        return ResultUtils.success(res);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateApplication(@RequestBody ApplicationUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = applicationService.updateApplication(updateRequest, loginUser);
        return ResultUtils.success(res);
    }

    @GetMapping("/get")
    public BaseResponse<Application> getApplicationById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Application app = applicationService.getById(id);
        return ResultUtils.success(app);
    }

    @GetMapping("/get/vo")
    public BaseResponse<ApplicationVO> getApplicationVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Application app = applicationService.getById(id);
        return ResultUtils.success(applicationService.getApplicationVO(app));
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ApplicationVO>> listApplicationVOByPage(@RequestBody ApplicationQueryRequest queryRequest, HttpServletRequest request) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        
        if (!isAdmin) {
             if (UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole())) {
                 queryRequest.setBossId(loginUser.getId());
             } else {
                 queryRequest.setUserId(loginUser.getId());
             }
        }
        
        Page<ApplicationVO> page = applicationService.listApplicationVOByPage(queryRequest);
        return ResultUtils.success(page);
    }
}
