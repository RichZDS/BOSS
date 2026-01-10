package com.zds.boss.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zds.boss.common.BaseResponse;
import com.zds.boss.common.ResultUtils;
import com.zds.boss.model.entity.User;
import com.zds.boss.model.enums.UserRoleEnum;
import com.zds.boss.model.vo.UserVO;
import com.zds.boss.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/boss")
public class BossController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listBoss() {
        List<User> userList = userService.list(new QueryWrapper<>(new User()).eq("user_role", UserRoleEnum.BOSS.getValue()));
        List<UserVO> userVOList = userService.getUserVOList(userList);
        return ResultUtils.success(userVOList);
    }
}

