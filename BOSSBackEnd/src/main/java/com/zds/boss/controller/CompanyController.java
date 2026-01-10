package com.zds.boss.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.annotation.AuthCheck;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.DeleteRequest;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.dto.company.CompanyAddRequest;
import com.zds.boss.model.dto.company.CompanyQueryRequest;
import com.zds.boss.model.dto.company.CompanyUpdateRequest;
import com.zds.boss.model.entity.Company;
import com.zds.boss.model.vo.CompanyVO;
import com.zds.boss.service.CompanyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/company")
@Slf4j
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @PostMapping("/add")
    public BaseResponse<Long> addCompany(@RequestBody CompanyAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = companyService.addCompany(addRequest, request);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCompany(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = companyService.deleteCompany(deleteRequest.getId(), request);
        return ResultUtils.success(res);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateCompany(@RequestBody CompanyUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = companyService.updateCompany(updateRequest, request);
        return ResultUtils.success(res);
    }

    @GetMapping("/get")
    public BaseResponse<Company> getCompanyById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Company company = companyService.getById(id);
        return ResultUtils.success(company);
    }

    @GetMapping("/get/vo")
    public BaseResponse<CompanyVO> getCompanyVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Company company = companyService.getById(id);
        return ResultUtils.success(companyService.getCompanyVO(company));
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CompanyVO>> listCompanyVOByPage(@RequestBody CompanyQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long size = queryRequest.getPageSize();
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分页大小过大");
        }
        Page<CompanyVO> page = companyService.listCompanyVOByPage(queryRequest);
        return ResultUtils.success(page);
    }
}
