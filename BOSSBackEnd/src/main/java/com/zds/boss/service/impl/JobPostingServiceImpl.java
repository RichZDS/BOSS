package com.zds.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.mapper.JobPostingMapper;
import com.zds.boss.model.dto.job.JobPostingAddRequest;
import com.zds.boss.model.dto.job.JobPostingQueryRequest;
import com.zds.boss.model.dto.job.JobPostingUpdateRequest;
import com.zds.boss.model.entity.JobPosting;
import com.zds.boss.model.vo.JobPostingVO;
import com.zds.boss.service.JobPostingService;
import com.zds.boss.constant.UserConstant;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPostingServiceImpl extends ServiceImpl<JobPostingMapper, JobPosting> implements JobPostingService {

    @Resource
    @Lazy
    private UserService userService;

    @Override
    public long addJobPosting(JobPostingAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null || StrUtil.isBlank(addRequest.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        
        // 权限检查
        if (!UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole()) && !UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
             throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非Boss权限");
        }

        JobPosting job = new JobPosting();
        BeanUtil.copyProperties(addRequest, job);
        job.setBossId(loginUser.getId());
        job.setCompanyId(loginUser.getCompanyId());
        
        if (job.getCompanyId() == null || job.getCompanyId() <= 0) {
             throw new BusinessException(ErrorCode.OPERATION_ERROR, "请先加入或创建公司");
        }
        
        boolean res = this.save(job);
        if (!res) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return job.getId();
    }

    @Override
    public boolean deleteJobPosting(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        
        if (!isAdmin) {
             JobPosting job = this.getById(id);
             if (job == null) {
                 throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
             }
             if (!loginUser.getId().equals(job.getBossId())) {
                 throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
             }
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateJobPosting(JobPostingUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        JobPosting job = this.getById(updateRequest.getId());
        if (job == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        
        if (!isAdmin) {
            if (!loginUser.getId().equals(job.getBossId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        BeanUtil.copyProperties(updateRequest, job);
        return this.updateById(job);
    }

    @Override
    public JobPostingVO getJobPostingVO(JobPosting job) {
        if (job == null) {
            return null;
        }
        JobPostingVO vo = new JobPostingVO();
        BeanUtil.copyProperties(job, vo);
        return vo;
    }

    @Override
    public List<JobPostingVO> getJobPostingVOList(List<JobPosting> list) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::getJobPostingVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<JobPosting> getQueryWrapper(JobPostingQueryRequest query) {
        if (query == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = query.getId();
        Long bossId = query.getBossId();
        Long companyId = query.getCompanyId();
        String title = query.getTitle();
        String location = query.getLocation();
        String jobType = query.getJobType();
        Integer status = query.getStatus();
        String sortField = query.getSortField();
        String sortOrder = query.getSortOrder();

        QueryWrapper<JobPosting> qw = new QueryWrapper<>();
        qw.eq(ObjUtil.isNotNull(id), "id", id);
        qw.eq(ObjUtil.isNotNull(bossId), "boss_id", bossId);
        qw.eq(ObjUtil.isNotNull(companyId), "company_id", companyId);
        qw.eq(ObjUtil.isNotNull(status), "status", status);
        qw.like(StrUtil.isNotBlank(title), "title", title);
        qw.like(StrUtil.isNotBlank(location), "location", location);
        qw.like(StrUtil.isNotBlank(jobType), "job_type", jobType);
        qw.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return qw;
    }

    @Override
    public Page<JobPostingVO> listJobPostingVOByPage(JobPostingQueryRequest query) {
        long current = query.getCurrent();
        long size = query.getPageSize();
        Page<JobPosting> page = this.page(new Page<>(current, size), getQueryWrapper(query));
        Page<JobPostingVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(getJobPostingVOList(page.getRecords()));
        return voPage;
    }
}

