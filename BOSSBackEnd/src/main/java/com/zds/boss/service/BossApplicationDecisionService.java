package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.decision.BossApplicationDecisionAddRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionQueryRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionUpdateRequest;
import com.zds.boss.model.entity.BossApplicationDecision;
import com.zds.boss.model.vo.BossApplicationDecisionVO;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BossApplicationDecisionService extends IService<BossApplicationDecision> {
    long addDecision(BossApplicationDecisionAddRequest addRequest, HttpServletRequest request);
    boolean deleteDecision(long id, HttpServletRequest request);
    boolean updateDecision(BossApplicationDecisionUpdateRequest updateRequest, HttpServletRequest request);
    BossApplicationDecisionVO getDecisionVO(BossApplicationDecision decision);
    List<BossApplicationDecisionVO> getDecisionVOList(List<BossApplicationDecision> list);
    QueryWrapper<BossApplicationDecision> getQueryWrapper(BossApplicationDecisionQueryRequest query);
    Page<BossApplicationDecisionVO> listDecisionVOByPage(BossApplicationDecisionQueryRequest query);
}

