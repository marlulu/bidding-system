package com.bidding.controller;

import com.bidding.common.LoginUser;
import com.bidding.common.Result;
import com.bidding.dto.LoginRequest;
import com.bidding.dto.RegisterRequest;
import com.bidding.dto.ResetPasswordRequest;
import com.bidding.dto.ChangePasswordRequest;
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

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return Result.success("注册成功，请等待管理员审核", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 忘记密码/重置密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {}

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userId, request);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 忘记密码/重置密码
     */
        try {
            userService.resetPassword(request);
            return Result.success("密码重置成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
