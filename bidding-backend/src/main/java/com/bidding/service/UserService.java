package com.bidding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.Constants;
import com.bidding.dto.ChangePasswordRequest;
import com.bidding.dto.LoginRequest;
import com.bidding.dto.RegisterRequest;
import com.bidding.dto.ResetPasswordRequest;
import com.bidding.entity.Supplier;
import com.bidding.entity.User;
import com.bidding.mapper.SupplierMapper;
import com.bidding.mapper.UserMapper;
import com.bidding.util.JwtUtil;
import com.bidding.util.PasswordUtil;
import com.bidding.vo.LoginVO;
import com.bidding.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    public LoginVO login(LoginRequest request) {
        log.info("尝试登录用户: {}", request.getUsername());
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            log.error("登录失败: 用户 {} 不存在", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        log.info("用户存在 (ID: {}), 开始验证密码...", user.getId());
        if (!PasswordUtil.verify(request.getPassword(), user.getPassword())) {
            log.error("登录失败: 用户 {} 密码错误", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus().equals(Constants.USER_STATUS_DISABLED)) {
            log.error("登录失败: 用户 {} 账号已被禁用", request.getUsername());
            throw new RuntimeException("账号已被禁用");
        }

        log.info("登录成功: {} (ID: {})", request.getUsername(), user.getId());
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建返回数据
        UserVO userVO = convertToVO(user);
        return new LoginVO(token, userVO);
    }

    /**
     * 获取用户信息
     */
    public UserVO getUserInfo(Long userId) {
        log.info("正在获取用户信息, userId: {}", userId);
        if (userId == null) {
            log.error("获取用户信息失败: userId 为空");
            throw new RuntimeException("用户未登录");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.error("获取用户信息失败: 数据库中找不到 ID 为 {} 的用户", userId);
            throw new RuntimeException("用户不存在");
        }
        
        log.info("成功获取用户信息: {}", user.getUsername());
        return convertToVO(user);
    }

    /**
     * 获取用户列表（分页，支持按用户名、角色、状态筛选）
     */
    public IPage<User> getUserList(Page<User> page, String username, String role, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 创建用户
     */
    @Transactional
    public void createUser(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        user.setStatus(Constants.USER_STATUS_ENABLED); // 默认启用
        userMapper.insert(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public void updateUser(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果修改了密码，需要加密
        if (StringUtils.hasText(user.getPassword()) && 
            !user.getPassword().equals(existUser.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        } else {
            user.setPassword(null); // 不修改密码
        }

        userMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 更新用户角色
     */
    @Transactional
    public void updateUserRole(Long id, String role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        userMapper.updateById(user);
    }

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encrypt(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setRole(request.getRole());
        user.setSupplierId(request.getSupplierId());
        user.setStatus(Constants.USER_STATUS_PENDING_REVIEW); // 注册用户默认为待审核状态
        userMapper.insert(user);
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(PasswordUtil.encrypt(request.getNewPassword()));
        userMapper.updateById(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtil.verify(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 更新新密码
        user.setPassword(PasswordUtil.encrypt(request.getNewPassword()));
        userMapper.updateById(user);
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        // 如果是供应商角色，获取供应商名称
        if (user.getSupplierId() != null) {
            Supplier supplier = supplierMapper.selectById(user.getSupplierId());
            if (supplier != null) {
                vo.setSupplierName(supplier.getCompanyName());
            }
        }
        
        return vo;
    }
}
