package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.annotation.AuthCheck;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.dto.job.JobPostingAddRequest;
import com.zds.boss.model.dto.job.JobPostingQueryRequest;
import com.zds.boss.model.dto.job.JobPostingUpdateRequest;
import com.zds.boss.model.entity.JobPosting;
import com.zds.boss.model.vo.JobPostingVO;
import com.zds.boss.service.JobPostingService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job")
@Slf4j
public class JobPostingController {

    @Resource
    private JobPostingService jobPostingService;

    @PostMapping("/add")
    public BaseResponse<Long> addJob(@RequestBody JobPostingAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = jobPostingService.addJobPosting(addRequest, request);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteJob(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = jobPostingService.deleteJobPosting(deleteRequest.getId(), request);
        return ResultUtils.success(res);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateJob(@RequestBody JobPostingUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = jobPostingService.updateJobPosting(updateRequest, request);
        return ResultUtils.success(res);
    }

    @GetMapping("/get")
    public BaseResponse<JobPosting> getJobById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        JobPosting job = jobPostingService.getById(id);
        return ResultUtils.success(job);
    }

    @GetMapping("/get/vo")
    public BaseResponse<JobPostingVO> getJobVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        JobPosting job = jobPostingService.getById(id);
        return ResultUtils.success(jobPostingService.getJobPostingVO(job));
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<JobPostingVO>> listJobVOByPage(@RequestBody JobPostingQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<JobPostingVO> page = jobPostingService.listJobPostingVOByPage(queryRequest);
        return ResultUtils.success(page);
    }
}
