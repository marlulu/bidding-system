package com.bidding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.Constants;
import com.bidding.common.PageResult;
import com.bidding.dto.LoginRequest;
import com.bidding.entity.Supplier;
import com.bidding.entity.User;
import com.bidding.mapper.SupplierMapper;
import com.bidding.mapper.UserMapper;
import com.bidding.util.JwtUtil;
import com.bidding.util.PasswordUtil;
import com.bidding.vo.LoginVO;
import com.bidding.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!PasswordUtil.verify(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus().equals(Constants.USER_STATUS_DISABLED)) {
            throw new RuntimeException("账号已被禁用");
        }

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
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 获取用户列表
     */
    public PageResult<UserVO> getUserList(Integer page, Integer size, String keyword, String role) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        
        return new PageResult<>(
                result.getTotal(),
                result.getRecords().stream().map(this::convertToVO).toList()
        );
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
