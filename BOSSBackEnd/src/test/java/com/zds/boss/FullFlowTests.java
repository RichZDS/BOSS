package com.zds.boss;

import com.zds.boss.constant.UserConstant;
import com.zds.boss.model.dto.application.ApplicationAddRequest;
import com.zds.boss.model.dto.company.CompanyAddRequest;
import com.zds.boss.model.dto.decision.BossApplicationDecisionAddRequest;
import com.zds.boss.model.dto.job.JobPostingAddRequest;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.LoginUserVO;
import com.zds.boss.model.dto.interview.InterviewAddRequest;
import com.zds.boss.service.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.Date;

@SpringBootTest
class FullFlowTests {

    @Resource
    private UserService userService;
    @Resource
    private CompanyService companyService;
    @Resource
    private JobPostingService jobPostingService;
    @Resource
    private ApplicationService applicationService;
    @Resource
    private BossApplicationDecisionService decisionService;
    @Resource
    private InterviewService interviewService;

    @Test
    void testFullRecruitmentFlow() {
        // --- 1. 注册 Boss ---
        String bossAccount = "boss_" + (System.currentTimeMillis() % 100000);
        String password = "password123";
        long bossId = userService.userRegister(bossAccount, password, password, "2"); // 2 for Boss
        Assertions.assertTrue(bossId > 0);

        // Boss 登录
        MockHttpServletRequest bossRequest = new MockHttpServletRequest();
        MockHttpSession bossSession = new MockHttpSession();
        bossRequest.setSession(bossSession);
        
        LoginUserVO bossVO = userService.userLogin(bossAccount, password, bossRequest);
        Assertions.assertEquals(UserRoleEnum.BOSS.getValue(), bossVO.getUserRole());
        
        // --- 2. Boss 创建公司 ---
        CompanyAddRequest companyReq = new CompanyAddRequest();
        companyReq.setName("Tech Corp " + System.currentTimeMillis());
        companyReq.setIndustry("IT");
        long companyId = companyService.addCompany(companyReq, bossRequest);
        Assertions.assertTrue(companyId > 0);
        
        // 验证 Boss 关联了公司
        User bossUser = userService.getById(bossId);
        Assertions.assertEquals(companyId, bossUser.getCompanyId());
        
        // --- 3. Boss 发布职位 ---
        JobPostingAddRequest jobReq = new JobPostingAddRequest();
        jobReq.setTitle("Java Developer");
        jobReq.setDescription("Write Java code");
        jobReq.setRequirement("Know Spring Boot");
        // 更新 request 中的 user session (因为 addCompany 更新了 user)
        bossSession.setAttribute(UserConstant.USER_LOGIN_STATE, bossUser); 
        
        long jobId = jobPostingService.addJobPosting(jobReq, bossRequest);
        Assertions.assertTrue(jobId > 0);
        
        // --- 4. 注册求职者 ---
        String userAccount = "user_" + (System.currentTimeMillis() % 100000);
        long userId = userService.userRegister(userAccount, password, password, "1"); // 1 for User
        Assertions.assertTrue(userId > 0);
        
        // 求职者登录
        MockHttpServletRequest userRequest = new MockHttpServletRequest();
        MockHttpSession userSession = new MockHttpSession();
        userRequest.setSession(userSession);
        LoginUserVO userVO = userService.userLogin(userAccount, password, userRequest);
        User userUser = userService.getById(userId);
        
        // --- 5. 求职者投递 ---
        ApplicationAddRequest appReq = new ApplicationAddRequest();
        appReq.setJobId(jobId);
        long appId = applicationService.addApplication(appReq, userUser);
        Assertions.assertTrue(appId > 0);
        
        // --- 6. Boss 处理投递 ---
        BossApplicationDecisionAddRequest decisionReq = new BossApplicationDecisionAddRequest();
        decisionReq.setApplicationId(appId);
        decisionReq.setDecision(1); // Accept
        decisionReq.setStage(1); // Interview
        
        long decisionId = decisionService.addDecision(decisionReq, bossRequest);
        Assertions.assertTrue(decisionId > 0);

        // --- 7. Boss 发起面试 ---
        InterviewAddRequest interviewReq = new InterviewAddRequest();
        interviewReq.setApplicationId(appId);
        interviewReq.setUserId(userId);
        interviewReq.setInterviewTime(new Date());
        interviewReq.setMode(0); // Online
        interviewReq.setLocation("Zoom Link");
        
        long interviewId = interviewService.addInterview(interviewReq, bossRequest);
        Assertions.assertTrue(interviewId > 0);
        
        System.out.println("Full flow test passed!");
    }
}
