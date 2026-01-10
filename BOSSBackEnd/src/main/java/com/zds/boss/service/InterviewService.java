package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.interview.InterviewAddRequest;
import com.zds.boss.model.dto.interview.InterviewQueryRequest;
import com.zds.boss.model.dto.interview.InterviewUpdateRequest;
import com.zds.boss.model.entity.Interview;
import com.zds.boss.model.vo.InterviewVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface InterviewService extends IService<Interview> {
    long addInterview(InterviewAddRequest addRequest, HttpServletRequest request);
    boolean deleteInterview(long id);
    boolean updateInterview(InterviewUpdateRequest updateRequest);
    InterviewVO getInterviewVO(Interview interview);
    List<InterviewVO> getInterviewVOList(List<Interview> list);
    QueryWrapper<Interview> getQueryWrapper(InterviewQueryRequest query);
    Page<InterviewVO> listInterviewVOByPage(InterviewQueryRequest query);
}

