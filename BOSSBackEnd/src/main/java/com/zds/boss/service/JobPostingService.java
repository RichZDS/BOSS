package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.job.JobPostingAddRequest;
import com.zds.boss.model.dto.job.JobPostingQueryRequest;
import com.zds.boss.model.dto.job.JobPostingUpdateRequest;
import com.zds.boss.model.entity.JobPosting;
import com.zds.boss.model.vo.JobPostingVO;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface JobPostingService extends IService<JobPosting> {
    long addJobPosting(JobPostingAddRequest addRequest, HttpServletRequest request);
    boolean deleteJobPosting(long id, HttpServletRequest request);
    boolean updateJobPosting(JobPostingUpdateRequest updateRequest, HttpServletRequest request);
    JobPostingVO getJobPostingVO(JobPosting job);
    List<JobPostingVO> getJobPostingVOList(List<JobPosting> list);
    QueryWrapper<JobPosting> getQueryWrapper(JobPostingQueryRequest query);
    Page<JobPostingVO> listJobPostingVOByPage(JobPostingQueryRequest query);
}

