package com.zds.boss.service;

import com.zds.boss.model.dto.resume.ResumeAiOptimizeRequest;
import com.zds.boss.model.vo.ResumeAiOptimizeVO;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * 使用AI优化简历
     *
     * @param request 简历优化请求
     * @return 优化后的简历内容
     */
    ResumeAiOptimizeVO optimizeResume(ResumeAiOptimizeRequest request);
}
