package com.zds.boss;

import com.zds.boss.model.vo.LoginUserVO;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
class BossApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void testRegisterAndLogin() {
        String account = "test" + (System.currentTimeMillis() % 1000); // ensure short enough
        String password = "password123";
        
        // 1. Register
        long userId = userService.userRegister(account, password, password, "1");
        Assertions.assertTrue(userId > 0);

        // 2. Login
        MockHttpServletRequest request = new MockHttpServletRequest();
        LoginUserVO loginUserVO = userService.userLogin(account, password, request);
        
        // 3. Verify
        Assertions.assertNotNull(loginUserVO);
        Assertions.assertEquals(account, loginUserVO.getUserAccount());
        // Verify default role
        Assertions.assertEquals("user", loginUserVO.getUserRole());
        
        System.out.println("Test passed! User registered and logged in successfully. Role: " + loginUserVO.getUserRole());
    }

}
