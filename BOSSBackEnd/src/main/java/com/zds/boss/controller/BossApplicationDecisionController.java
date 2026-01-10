package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.dto.decision.BossApplicationDecisionAddRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionQueryRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionUpdateRequest;
import com.zds.boss.model.entity.BossApplicationDecision;
import com.zds.boss.model.vo.BossApplicationDecisionVO;
import com.zds.boss.service.BossApplicationDecisionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/decision")
@Slf4j
public class BossApplicationDecisionController {

    @Resource
    private BossApplicationDecisionService decisionService;

    @PostMapping("/add")
    public BaseResponse<Long> addDecision(@RequestBody BossApplicationDecisionAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = decisionService.addDecision(addRequest, request);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDecision(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = decisionService.deleteDecision(deleteRequest.getId(), request);
        return ResultUtils.success(res);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateDecision(@RequestBody BossApplicationDecisionUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = decisionService.updateDecision(updateRequest, request);
        return ResultUtils.success(res);
    }

    @GetMapping("/get")
    public BaseResponse<BossApplicationDecision> getDecisionById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BossApplicationDecision decision = decisionService.getById(id);
        return ResultUtils.success(decision);
    }

    @GetMapping("/get/vo")
    public BaseResponse<BossApplicationDecisionVO> getDecisionVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BossApplicationDecision decision = decisionService.getById(id);
        return ResultUtils.success(decisionService.getDecisionVO(decision));
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BossApplicationDecisionVO>> listDecisionVOByPage(@RequestBody BossApplicationDecisionQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<BossApplicationDecisionVO> page = decisionService.listDecisionVOByPage(queryRequest);
        return ResultUtils.success(page);
    }
}
