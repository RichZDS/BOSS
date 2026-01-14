package com.zds.boss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.mapper.ApplicationMapper;
import com.zds.boss.model.dto.application.ApplicationAddRequest;
import com.zds.boss.model.dto.application.ApplicationQueryRequest;
import com.zds.boss.model.dto.application.ApplicationUpdateRequest;
import com.zds.boss.model.entity.Application;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.vo.ApplicationVO;
import com.zds.boss.service.ApplicationService;
import com.zds.boss.service.JobPostingService;
import com.zds.boss.model.entity.JobPosting;
import com.zds.boss.utils.UserUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Resource
    @Lazy
    private JobPostingService jobPostingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addApplication(ApplicationAddRequest addRequest, User loginUser) {
        if (addRequest == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检查是否已投递
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        queryWrapper.eq("job_id", addRequest.getJobId());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经投递过该职位");
        }

        Application app = new Application();
        BeanUtil.copyProperties(addRequest, app);
        app.setUserId(loginUser.getId());
        
        // 补充 bossId (从 JobPosting 获取)
        if (app.getJobId() != null) {
            JobPosting job = jobPostingService.getById(app.getJobId());
            if (job != null) {
                app.setBossId(job.getBossId());
            }
        }
        
        boolean res = this.save(app);
        if (!res) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return app.getId();
    }

    @Override
    public boolean deleteApplication(long id, User loginUser) {
        Application app = this.getById(id);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 管理员可以删除
        if (UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
            return this.removeById(id);
        }
        
        // 本人可以删除
        if (app.getUserId().equals(loginUser.getId())) {
            return this.removeById(id);
        }
        
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }

    @Override
    public boolean updateApplication(ApplicationUpdateRequest updateRequest, User loginUser) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Application app = this.getById(updateRequest.getId());
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 管理员可以修改
        if (UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
            BeanUtil.copyProperties(updateRequest, app);
            return this.updateById(app);
        }
        
        // 本人可以修改
        if (app.getUserId().equals(loginUser.getId())) {
            BeanUtil.copyProperties(updateRequest, app);
            return this.updateById(app);
        }
        
        // Boss (招聘方) 也可以修改 (例如修改状态)
        if (app.getBossId() != null && app.getBossId().equals(loginUser.getId())) {
             BeanUtil.copyProperties(updateRequest, app);
             return this.updateById(app);
        }
        
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }

    @Override
    @Transactional
    public boolean updateApplicationStatus(long applicationId, int status) {
        if (applicationId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "申请ID不能为空");
        }
        if (status != 1 && status != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态值必须为1(接受)或2(拒绝)");
        }
        
        Application app = this.getById(applicationId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "申请不存在");
        }
        
        app.setStatus(status);
        return this.updateById(app);
    }

    @Override
    public ApplicationVO getApplicationVO(Application app) {
        if (app == null) {
            return null;
        }
        ApplicationVO vo = new ApplicationVO();
        BeanUtil.copyProperties(app, vo);
        return vo;
    }

    @Override
    public List<ApplicationVO> getApplicationVOList(List<Application> list) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::getApplicationVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<Application> getQueryWrapper(ApplicationQueryRequest query) {
        if (query == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = query.getId();
        Long userId = query.getUserId();
        Long resumeId = query.getResumeId();
        Long jobId = query.getJobId();
        Long bossId = query.getBossId();
        Integer status = query.getStatus();
        String sortField = query.getSortField();
        String sortOrder = query.getSortOrder();

        QueryWrapper<Application> qw = new QueryWrapper<>();
        qw.eq(ObjUtil.isNotNull(id), "id", id);
        qw.eq(ObjUtil.isNotNull(userId), "user_id", userId);
        qw.eq(ObjUtil.isNotNull(resumeId), "resume_id", resumeId);
        qw.eq(ObjUtil.isNotNull(jobId), "job_id", jobId);
        qw.eq(ObjUtil.isNotNull(bossId), "boss_id", bossId);
        qw.eq(ObjUtil.isNotNull(status), "status", status);
        qw.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return qw;
    }

    @Override
    public Page<ApplicationVO> listApplicationVOByPage(ApplicationQueryRequest query) {
        long current = query.getCurrent();
        long size = query.getPageSize();
        Page<ApplicationVO> voPage = new Page<>(current, size);
        this.baseMapper.selectApplicationVOByPage(voPage, query);
        return voPage;
    }
}

