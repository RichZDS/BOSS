package com.zds.boss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zds.boss.model.entity.ResumeAddress;

/**
 * 简历地址服务
 */
public interface ResumeAddressService extends IService<ResumeAddress> {

    /**
     * 根据用户ID获取简历地址
     *
     * @param userId 用户ID
     * @return 简历地址
     */
    ResumeAddress getByUserId(Long userId);

    /**
     * 保存或更新简历地址
     *
     * @param userId   用户ID
     * @param resumeId 简历ID（可为空）
     * @param address  文件URL
     * @param fileKey  文件Key
     * @return 简历地址
     */
    ResumeAddress saveOrUpdateByUserId(Long userId, Long resumeId, String address, String fileKey);

    /**
     * 根据用户ID删除简历地址
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteByUserId(Long userId);
}
