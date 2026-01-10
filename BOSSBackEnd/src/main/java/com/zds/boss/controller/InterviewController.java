package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.annotation.AuthCheck;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.dto.interview.InterviewAddRequest;
import com.zds.boss.model.dto.interview.InterviewQueryRequest;
import com.zds.boss.model.dto.interview.InterviewUpdateRequest;
import com.zds.boss.model.entity.Interview;
import com.zds.boss.model.vo.InterviewVO;
import com.zds.boss.service.InterviewService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview")
@Slf4j
public class InterviewController {

    @Resource
    private InterviewService interviewService;

    @PostMapping("/add")
    public BaseResponse<Long> addInterview(@RequestBody InterviewAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interviewService.addInterview(addRequest, request);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteInterview(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = interviewService.deleteInterview(deleteRequest.getId());
        return ResultUtils.success(res);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterview(@RequestBody InterviewUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = interviewService.updateInterview(updateRequest);
        return ResultUtils.success(res);
    }

    @GetMapping("/get")
    public BaseResponse<Interview> getInterviewById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interview e = interviewService.getById(id);
        return ResultUtils.success(e);
    }

    @GetMapping("/get/vo")
    public BaseResponse<InterviewVO> getInterviewVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interview e = interviewService.getById(id);
        return ResultUtils.success(interviewService.getInterviewVO(e));
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterviewVO>> listInterviewVOByPage(@RequestBody InterviewQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<InterviewVO> page = interviewService.listInterviewVOByPage(queryRequest);
        return ResultUtils.success(page);
    }
}
