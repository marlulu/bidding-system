package com.bidding.controller;

import com.bidding.common.LoginUser;
import com.bidding.common.Result;
import com.bidding.dto.LoginRequest;
import com.bidding.service.UserService;
import com.bidding.vo.LoginVO;
import com.bidding.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginVO loginVO = userService.login(request);
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("登出成功", null);
    }
}
