package com.zds.boss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zds.boss.mapper.ResumeAddressMapper;
import com.zds.boss.model.entity.ResumeAddress;
import com.zds.boss.service.ResumeAddressService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 简历地址服务实现
 */
@Service
public class ResumeAddressServiceImpl extends ServiceImpl<ResumeAddressMapper, ResumeAddress>
        implements ResumeAddressService {

    @Override
    public ResumeAddress getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<ResumeAddress>()
                .eq(ResumeAddress::getUserId, userId));
    }

    @Override
    public ResumeAddress saveOrUpdateByUserId(Long userId, Long resumeId, String address, String fileKey) {
        ResumeAddress existing = getByUserId(userId);
        
        if (existing != null) {
            // 更新现有记录
            existing.setResumeId(resumeId);
            existing.setAddress(address);
            existing.setFileKey(fileKey);
            existing.setUpdatedAt(new Date());
            this.updateById(existing);
            return existing;
        } else {
            // 创建新记录
            ResumeAddress resumeAddress = new ResumeAddress();
            resumeAddress.setUserId(userId);
            resumeAddress.setResumeId(resumeId);
            resumeAddress.setAddress(address);
            resumeAddress.setFileKey(fileKey);
            resumeAddress.setCreatedAt(new Date());
            resumeAddress.setUpdatedAt(new Date());
            this.save(resumeAddress);
            return resumeAddress;
        }
    }

    @Override
    public boolean deleteByUserId(Long userId) {
        return this.remove(new LambdaQueryWrapper<ResumeAddress>()
                .eq(ResumeAddress::getUserId, userId));
    }
}
