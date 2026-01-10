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
import com.zds.boss.mapper.BossApplicationDecisionMapper;
import com.zds.boss.model.dto.decision.BossApplicationDecisionAddRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionQueryRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionUpdateRequest;
import com.zds.boss.model.entity.BossApplicationDecision;
import com.zds.boss.model.vo.BossApplicationDecisionVO;
import com.zds.boss.service.BossApplicationDecisionService;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.service.UserService;
import com.zds.boss.service.ApplicationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BossApplicationDecisionServiceImpl extends ServiceImpl<BossApplicationDecisionMapper, BossApplicationDecision>
        implements BossApplicationDecisionService {

    @Resource
    private UserService userService;
    
    @Resource
    private ApplicationService applicationService;

    @Override
    @Transactional
    public long addDecision(BossApplicationDecisionAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null || addRequest.getApplicationId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 验证决策值是否有效
        if (addRequest.getDecision() == null || (addRequest.getDecision() != 1 && addRequest.getDecision() != 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "决策值必须为1(接受)或2(拒绝)");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 检查是否已存在决策记录，如果有则更新，否则插入新记录
        QueryWrapper<BossApplicationDecision> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", addRequest.getApplicationId());
        BossApplicationDecision existingDecision = this.getOne(queryWrapper);
        
        BossApplicationDecision e;
        boolean isNew = true;
        
        if (existingDecision != null) {
            // 更新现有记录
            e = existingDecision;
            isNew = false;
            BeanUtil.copyProperties(addRequest, e);
            e.setBossId(loginUser.getId());
            e.setDecidedAt(new Date()); // 更新决策时间
            boolean res = this.updateById(e);
            if (!res) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        } else {
            // 插入新记录
            e = new BossApplicationDecision();
            BeanUtil.copyProperties(addRequest, e);
            e.setBossId(loginUser.getId());
            e.setDecidedAt(new Date()); // 设置决策时间
            boolean res = this.save(e);
            if (!res) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        }
        
        // 更新申请状态
        try {
            applicationService.updateApplicationStatus(addRequest.getApplicationId(), addRequest.getDecision());
        } catch (Exception ex) {
            log.error("更新申请状态失败", ex);
            // 不抛出异常，因为决策已经处理成功
        }
        
        return e.getId();
    }

    @Override
    public boolean deleteDecision(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BossApplicationDecision decision = this.getById(id);
        if (decision == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        if (!isAdmin) {
            if (!loginUser.getId().equals(decision.getBossId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateDecision(BossApplicationDecisionUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BossApplicationDecision decision = this.getById(updateRequest.getId());
        if (decision == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
        if (!isAdmin) {
            if (!loginUser.getId().equals(decision.getBossId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        BeanUtil.copyProperties(updateRequest, decision);
        return this.updateById(decision);
    }

    @Override
    public BossApplicationDecisionVO getDecisionVO(BossApplicationDecision decision) {
        if (decision == null) {
            return null;
        }
        BossApplicationDecisionVO vo = new BossApplicationDecisionVO();
        BeanUtil.copyProperties(decision, vo);
        return vo;
    }

    @Override
    public List<BossApplicationDecisionVO> getDecisionVOList(List<BossApplicationDecision> list) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::getDecisionVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<BossApplicationDecision> getQueryWrapper(BossApplicationDecisionQueryRequest query) {
        if (query == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = query.getId();
        Long applicationId = query.getApplicationId();
        Long bossId = query.getBossId();
        Integer decision = query.getDecision();
        Integer stage = query.getStage();
        String sortField = query.getSortField();
        String sortOrder = query.getSortOrder();

        QueryWrapper<BossApplicationDecision> qw = new QueryWrapper<>();
        qw.eq(ObjUtil.isNotNull(id), "id", id);
        qw.eq(ObjUtil.isNotNull(applicationId), "application_id", applicationId);
        qw.eq(ObjUtil.isNotNull(bossId), "boss_id", bossId);
        qw.eq(ObjUtil.isNotNull(decision), "decision", decision);
        qw.eq(ObjUtil.isNotNull(stage), "stage", stage);
        qw.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return qw;
    }

    @Override
    public Page<BossApplicationDecisionVO> listDecisionVOByPage(BossApplicationDecisionQueryRequest query) {
        long current = query.getCurrent();
        long size = query.getPageSize();
        Page<BossApplicationDecision> page = this.page(new Page<>(current, size), getQueryWrapper(query));
        Page<BossApplicationDecisionVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(getDecisionVOList(page.getRecords()));
        return voPage;
    }
}

