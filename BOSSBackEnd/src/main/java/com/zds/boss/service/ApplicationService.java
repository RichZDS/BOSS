package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.application.ApplicationAddRequest;
import com.zds.boss.model.dto.application.ApplicationQueryRequest;
import com.zds.boss.model.dto.application.ApplicationUpdateRequest;
import com.zds.boss.model.entity.Application;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.vo.ApplicationVO;

import java.util.List;

public interface ApplicationService extends IService<Application> {
    long addApplication(ApplicationAddRequest addRequest, User loginUser);
    boolean deleteApplication(long id, User loginUser);
    boolean updateApplication(ApplicationUpdateRequest updateRequest, User loginUser);
    boolean updateApplicationStatus(long applicationId, int status);
    ApplicationVO getApplicationVO(Application app);
    List<ApplicationVO> getApplicationVOList(List<Application> list);
    QueryWrapper<Application> getQueryWrapper(ApplicationQueryRequest query);
    Page<ApplicationVO> listApplicationVOByPage(ApplicationQueryRequest query);
}

