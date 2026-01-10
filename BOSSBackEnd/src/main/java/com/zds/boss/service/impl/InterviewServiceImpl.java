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
import com.zds.boss.mapper.InterviewMapper;
import com.zds.boss.model.dto.interview.InterviewAddRequest;
import com.zds.boss.model.dto.interview.InterviewQueryRequest;
import com.zds.boss.model.dto.interview.InterviewUpdateRequest;
import com.zds.boss.model.entity.Interview;
import com.zds.boss.model.vo.InterviewVO;
import com.zds.boss.service.InterviewService;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    @Resource
    private UserService userService;

    @Override
    public long addInterview(InterviewAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null || addRequest.getApplicationId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 权限：Admin 或 Boss
        if (!UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole()) && !UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole())) {
             throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        Interview e = new Interview();
        BeanUtil.copyProperties(addRequest, e);
        
        // 如果是 Boss，自动填充 bossId
        if (UserRoleEnum.BOSS.getValue().equals(loginUser.getUserRole())) {
            e.setBossId(loginUser.getId());
        }
        
        boolean res = this.save(e);
        if (!res) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return e.getId();
    }

    @Override
    public boolean deleteInterview(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateInterview(InterviewUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Interview e = new Interview();
        BeanUtil.copyProperties(updateRequest, e);
        return this.updateById(e);
    }

    @Override
    public InterviewVO getInterviewVO(Interview interview) {
        if (interview == null) {
            return null;
        }
        InterviewVO vo = new InterviewVO();
        BeanUtil.copyProperties(interview, vo);
        return vo;
    }

    @Override
    public List<InterviewVO> getInterviewVOList(List<Interview> list) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::getInterviewVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<Interview> getQueryWrapper(InterviewQueryRequest query) {
        if (query == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = query.getId();
        Long applicationId = query.getApplicationId();
        Long bossId = query.getBossId();
        Long userId = query.getUserId();
        Integer status = query.getStatus();
        String sortField = query.getSortField();
        String sortOrder = query.getSortOrder();

        QueryWrapper<Interview> qw = new QueryWrapper<>();
        qw.eq(ObjUtil.isNotNull(id), "id", id);
        qw.eq(ObjUtil.isNotNull(applicationId), "application_id", applicationId);
        qw.eq(ObjUtil.isNotNull(bossId), "boss_id", bossId);
        qw.eq(ObjUtil.isNotNull(userId), "user_id", userId);
        qw.eq(ObjUtil.isNotNull(status), "status", status);
        qw.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return qw;
    }

    @Override
    public Page<InterviewVO> listInterviewVOByPage(InterviewQueryRequest query) {
        long current = query.getCurrent();
        long size = query.getPageSize();
        Page<Interview> page = this.page(new Page<>(current, size), getQueryWrapper(query));
        Page<InterviewVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(getInterviewVOList(page.getRecords()));
        return voPage;
    }
}

