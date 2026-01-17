package com.bidding.controller;

import com.bidding.common.Result;
import com.bidding.entity.User;
import com.bidding.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import com.bidding.vo.UserVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页，支持按用户名、角色、状态筛选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param username 用户名（可选）
     * @param role 角色（可选）
     * @param status 状态（可选）
     * @return 用户列表
     */
    @GetMapping
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage = userService.getUserList(page, username, role, status);
        return Result.success(userPage);
    }

    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        UserVO userVO = userService.getUserInfo(id);
        User user = new User();
        org.springframework.beans.BeanUtils.copyProperties(userVO, user);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success("更新成功");
    }

    /**
     * 更新用户个人信息（用于 Profile 页面）
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}/info")
    public Result<String> updateUserInfo(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success("个人信息更新成功");
    }

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态 (0: 待审核, 1: 正常, 2: 禁用)
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 更新用户角色
     * @param id 用户ID
     * @param role 角色 (ADMIN, SUPPLIER)
     * @return 更新结果
     */
    @PutMapping("/{id}/role")
    public Result<String> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        userService.updateUserRole(id, role);
        return Result.success("角色更新成功");
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}
