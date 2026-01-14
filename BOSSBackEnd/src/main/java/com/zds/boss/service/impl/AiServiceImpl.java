package com.zds.boss.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zds.boss.exception.BusinessException;
import com.zds.boss.exception.ErrorCode;
import com.zds.boss.model.dto.resume.ResumeAiOptimizeRequest;
import com.zds.boss.model.vo.ResumeAiOptimizeVO;
import com.zds.boss.service.AiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.stereotype.Service;

/**
 * AI服务实现类
 */
@Service
@Slf4j
public class AiServiceImpl implements AiService {

    @Resource
    private DeepSeekChatModel deepSeekChatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 系统提示词 - 专业简历分析师
     */
    private static final String SYSTEM_PROMPT = """
            你是一位专业的简历分析师和职业规划顾问，拥有10年以上的人力资源和招聘经验。
            你的任务是帮助用户优化他们的简历，使其更加专业、吸引人，并能更好地展示他们的技能和经验。
            
            优化原则：
            1. 简历标题：简洁有力，突出核心职位和关键技能，格式如"职位名称-姓名"或"职位名称 | 核心优势"
            2. 个人摘要：控制在100-200字，突出核心竞争力、关键成就和职业目标，使用有力的动词开头
            3. 详细内容：使用STAR法则（情境-任务-行动-结果）描述经历，量化成就，突出与目标职位相关的技能
            
            优化要求：
            - 语言简洁专业，避免口语化表达
            - 突出数据和成果，使用具体数字量化成就
            - 使用行业关键词，提高ATS（简历筛选系统）通过率
            - 保持内容真实，在原有基础上进行润色和优化
            - 如果原内容较少，可以适当扩展，但要基于原有信息合理推断
            
            你必须严格按照以下JSON格式返回结果，不要包含任何其他内容：
            {
              "resumeTitle": "优化后的简历标题",
              "summary": "优化后的个人摘要",
              "content": "优化后的详细内容"
            }
            """;

    @Override
    public ResumeAiOptimizeVO optimizeResume(ResumeAiOptimizeRequest request) {
        log.info("开始AI简历优化，标题: {}", request.getResumeTitle());

        // 构建用户消息
        String userMessage = String.format("""
                请优化以下简历内容：
                
                【简历标题】
                %s
                
                【个人摘要】
                %s
                
                【详细内容】
                %s
                
                请按照JSON格式返回优化后的结果。
                """,
                nullToEmpty(request.getResumeTitle()),
                nullToEmpty(request.getSummary()),
                nullToEmpty(request.getContent())
        );

        try {
            // 调用DeepSeek API
            ChatClient chatClient = ChatClient.builder(deepSeekChatModel).build();
            String response = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userMessage)
                    .call()
                    .content();

            log.info("AI返回原始响应: {}", response);

            // 解析JSON响应
            ResumeAiOptimizeVO result = parseResponse(response);
            log.info("AI简历优化完成");
            return result;

        } catch (Exception e) {
            log.error("AI简历优化失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI优化失败，请稍后重试: " + e.getMessage());
        }
    }

    /**
     * 解析AI响应的JSON
     */
    private ResumeAiOptimizeVO parseResponse(String response) {
        try {
            // 尝试提取JSON部分（处理可能的markdown代码块）
            String jsonStr = extractJson(response);
            
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            
            ResumeAiOptimizeVO vo = new ResumeAiOptimizeVO();
            vo.setResumeTitle(getTextValue(jsonNode, "resumeTitle"));
            vo.setSummary(getTextValue(jsonNode, "summary"));
            vo.setContent(getTextValue(jsonNode, "content"));
            
            return vo;
        } catch (Exception e) {
            log.error("解析AI响应失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI响应解析失败");
        }
    }

    /**
     * 从响应中提取JSON字符串
     */
    private String extractJson(String response) {
        if (response == null || response.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI响应为空");
        }
        
        // 处理markdown代码块
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.lastIndexOf("```");
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            int end = response.lastIndexOf("```");
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        // 尝试找到JSON对象
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return response.trim();
    }

    /**
     * 安全获取JSON节点的文本值
     */
    private String getTextValue(JsonNode node, String field) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return "";
    }

    /**
     * 将null转换为空字符串
     */
    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
