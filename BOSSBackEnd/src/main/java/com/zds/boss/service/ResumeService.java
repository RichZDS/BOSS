package com.zds.boss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.dto.resume.ResumeAddRequest;
import com.zds.boss.model.dto.resume.ResumeQueryRequest;
import com.zds.boss.model.dto.resume.ResumeUpdateRequest;
import com.zds.boss.model.entity.Resume;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.vo.ResumeVO;

import java.util.List;

/**
* @author 33882
* @description 针对表【resume(简历表)】的数据库操作Service
* @createDate 2025-12-31 23:30:00
*/
public interface ResumeService extends IService<Resume> {

    /**
     * 创建简历
     *
     * @param resumeAddRequest
     * @param loginUser
     * @return
     */
    long addResume(ResumeAddRequest resumeAddRequest, User loginUser);

    /**
     * 删除简历
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteResume(long id, User loginUser);

    /**
     * 更新简历
     *
     * @param resumeUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateResume(ResumeUpdateRequest resumeUpdateRequest, User loginUser);

    /**
     * 获取简历VO
     *
     * @param resume
     * @return
     */
    ResumeVO getResumeVO(Resume resume);

    /**
     * 获取简历VO列表
     *
     * @param resumeList
     * @return
     */
    List<ResumeVO> getResumeVOList(List<Resume> resumeList);

    /**
     * 获取查询条件
     *
     * @param resumeQueryRequest
     * @return
     */
    QueryWrapper<Resume> getQueryWrapper(ResumeQueryRequest resumeQueryRequest);
    
    /**
     * 分页获取简历VO
     * @param resumeQueryRequest
     * @return
     */
    Page<ResumeVO> listResumeVOByPage(ResumeQueryRequest resumeQueryRequest);
}
