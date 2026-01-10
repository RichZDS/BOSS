package com.zds.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.mapper.CompanyMapper;
import com.zds.boss.model.dto.company.CompanyAddRequest;
import com.zds.boss.model.dto.company.CompanyQueryRequest;
import com.zds.boss.model.dto.company.CompanyUpdateRequest;
import com.zds.boss.model.entity.Company;
import com.zds.boss.model.vo.CompanyVO;
import com.zds.boss.service.CompanyService;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 33882
* @description 针对表【company(公司表)】的数据库操作Service实现
* @createDate 2025-12-31 23:47:37
*/
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company>
    implements CompanyService{

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addCompany(CompanyAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null || StrUtil.isBlank(addRequest.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        
        if (!isAdmin) {
             if (!UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole())) {
                 throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只有BOSS可以创建公司");
             }
             if (loginUser.getCompanyId() != null && loginUser.getCompanyId() > 0) {
                 throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经创建过公司，不能重复创建");
             }
        }
        
        Company company = new Company();
        BeanUtil.copyProperties(addRequest, company);
        boolean result = this.save(company);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        
        if (!isAdmin) {
            loginUser.setCompanyId(company.getId());
            boolean updateUser = userService.updateById(loginUser);
            if (!updateUser) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "绑定公司失败");
            }
            request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        }
        
        return company.getId();
    }

    @Override
    public boolean deleteCompany(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateCompany(CompanyUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        
        if (!isAdmin) {
            if (loginUser.getCompanyId() == null || !loginUser.getCompanyId().equals(updateRequest.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        
        Company company = new Company();
        BeanUtil.copyProperties(updateRequest, company);
        return this.updateById(company);
    }

    @Override
    public CompanyVO getCompanyVO(Company company) {
        if (company == null) {
            return null;
        }
        CompanyVO vo = new CompanyVO();
        BeanUtil.copyProperties(company, vo);
        return vo;
    }

    @Override
    public List<CompanyVO> getCompanyVOList(List<Company> companyList) {
        if (CollUtil.isEmpty(companyList)) {
            return new ArrayList<>();
        }
        return companyList.stream().map(this::getCompanyVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<Company> getQueryWrapper(CompanyQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = queryRequest.getId();
        String name = queryRequest.getName();
        String industry = queryRequest.getIndustry();
        Integer status = queryRequest.getStatus();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        QueryWrapper<Company> qw = new QueryWrapper<>();
        qw.eq(ObjUtil.isNotNull(id), "id", id);
        qw.eq(ObjUtil.isNotNull(status), "status", status);
        qw.like(StrUtil.isNotBlank(name), "name", name);
        qw.like(StrUtil.isNotBlank(industry), "industry", industry);
        qw.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return qw;
    }

    @Override
    public Page<CompanyVO> listCompanyVOByPage(CompanyQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<Company> page = this.page(new Page<>(current, size), getQueryWrapper(queryRequest));
        Page<CompanyVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(getCompanyVOList(page.getRecords()));
        return voPage;
    }
}

