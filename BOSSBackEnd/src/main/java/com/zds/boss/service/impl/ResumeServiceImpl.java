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
import com.zds.boss.exception.ThrowUtils;
import com.zds.boss.mapper.ResumeMapper;
import com.zds.boss.model.dto.resume.ResumeAddRequest;
import com.zds.boss.model.dto.resume.ResumeQueryRequest;
import com.zds.boss.model.dto.resume.ResumeUpdateRequest;
import com.zds.boss.model.entity.Resume;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.ResumeVO;
import com.zds.boss.service.ResumeService;
import com.zds.boss.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 33882
* @description 针对表【resume(简历表)】的数据库操作Service实现
* @createDate 2025-12-31 23:30:00
*/
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume>
    implements ResumeService {

    @Override
    public long addResume(ResumeAddRequest resumeAddRequest, User loginUser) {
        if (resumeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Resume resume = new Resume();
        BeanUtil.copyProperties(resumeAddRequest, resume);
        // 设置用户ID
        resume.setUserId(loginUser.getId());
        
        boolean result = this.save(resume);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return resume.getId();
    }

    @Override
    public boolean deleteResume(long id, User loginUser) {
        Resume resume = this.getById(id);
        if (resume == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!resume.getUserId().equals(loginUser.getId()) && !UserUtils.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 逻辑删除，MyBatis Plus会自动处理 is_deleted 字段
        return this.removeById(id);
    }

    @Override
    public boolean updateResume(ResumeUpdateRequest resumeUpdateRequest, User loginUser) {
        if (resumeUpdateRequest == null || resumeUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Resume resume = this.getById(resumeUpdateRequest.getId());
        if (resume == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!resume.getUserId().equals(loginUser.getId()) && !UserUtils.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        BeanUtil.copyProperties(resumeUpdateRequest, resume);
        return this.updateById(resume);
    }

    @Override
    public ResumeVO getResumeVO(Resume resume) {
        if (resume == null) {
            return null;
        }
        ResumeVO resumeVO = new ResumeVO();
        BeanUtil.copyProperties(resume, resumeVO);
        return resumeVO;
    }

    @Override
    public List<ResumeVO> getResumeVOList(List<Resume> resumeList) {
        if (CollUtil.isEmpty(resumeList)) {
            return new ArrayList<>();
        }
        return resumeList.stream().map(this::getResumeVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<Resume> getQueryWrapper(ResumeQueryRequest resumeQueryRequest) {
        if (resumeQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = resumeQueryRequest.getId();
        Long userId = resumeQueryRequest.getUserId();
        String resumeTitle = resumeQueryRequest.getResumeTitle();
        Integer isDefault = resumeQueryRequest.getIsDefault();
        String sortField = resumeQueryRequest.getSortField();
        String sortOrder = resumeQueryRequest.getSortOrder();

        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "user_id", userId);
        queryWrapper.eq(ObjUtil.isNotNull(isDefault), "is_default", isDefault);
        queryWrapper.like(StrUtil.isNotBlank(resumeTitle), "resume_title", resumeTitle);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        
        return queryWrapper;
    }

    @Override
    public Page<ResumeVO> listResumeVOByPage(ResumeQueryRequest resumeQueryRequest) {
        long current = resumeQueryRequest.getCurrent();
        long size = resumeQueryRequest.getPageSize();
        Page<Resume> resumePage = this.page(new Page<>(current, size), getQueryWrapper(resumeQueryRequest));
        Page<ResumeVO> resumeVOPage = new Page<>(current, size, resumePage.getTotal());
        List<ResumeVO> resumeVOList = getResumeVOList(resumePage.getRecords());
        resumeVOPage.setRecords(resumeVOList);
        return resumeVOPage;
    }
}
