package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zds.boss.model.entity.Company;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.company.CompanyAddRequest;
import com.zds.boss.model.dto.company.CompanyQueryRequest;
import com.zds.boss.model.dto.company.CompanyUpdateRequest;
import com.zds.boss.model.vo.CompanyVO;
import java.util.List;

/**
* @author 33882
* @description 针对表【company(公司表)】的数据库操作Service
* @createDate 2025-12-31 23:47:37
*/import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CompanyService extends IService<Company> {
    long addCompany(CompanyAddRequest addRequest, HttpServletRequest request);
    boolean deleteCompany(long id, HttpServletRequest request);
    boolean updateCompany(CompanyUpdateRequest updateRequest, HttpServletRequest request);
    CompanyVO getCompanyVO(Company company);

    List<CompanyVO> getCompanyVOList(List<Company> companyList);

    QueryWrapper<Company> getQueryWrapper(CompanyQueryRequest queryRequest);

    Page<CompanyVO> listCompanyVOByPage(CompanyQueryRequest queryRequest);
}
