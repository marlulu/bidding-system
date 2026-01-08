package com.bidding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.Constants;
import com.bidding.common.PageResult;
import com.bidding.entity.Policy;
import com.bidding.mapper.PolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    /**
     * 获取政策法规列表
     */
    public PageResult<Policy> getPolicyList(Integer page, Integer size, String keyword, 
                                             String category, String status, String role) {
        Page<Policy> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        
        // 供应商只能查看已发布的政策法规
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            wrapper.eq(Policy::getStatus, Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Policy::getTitle, keyword);
        }
        
        if (StringUtils.hasText(category)) {
            wrapper.eq(Policy::getCategory, category);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Policy::getStatus, status);
        }
        
        wrapper.orderByDesc(Policy::getCreateTime);
        Page<Policy> result = policyMapper.selectPage(pageParam, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    /**
     * 获取政策法规详情
     */
    public Policy getPolicyById(Long id, String role) {
        Policy policy = policyMapper.selectById(id);
        if (policy == null) {
            throw new RuntimeException("政策法规不存在");
        }
        
        // 供应商权限检查
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            if (!Constants.ANNOUNCEMENT_STATUS_PUBLISHED.equals(policy.getStatus())) {
                throw new RuntimeException("无权查看该政策法规");
            }
        }
        
        return policy;
    }

    /**
     * 创建政策法规
     */
    @Transactional
    public void createPolicy(Policy policy) {
        policy.setStatus(Constants.ANNOUNCEMENT_STATUS_DRAFT);
        policyMapper.insert(policy);
    }

    /**
     * 更新政策法规
     */
    @Transactional
    public void updatePolicy(Policy policy) {
        Policy existPolicy = policyMapper.selectById(policy.getId());
        if (existPolicy == null) {
            throw new RuntimeException("政策法规不存在");
        }

        policyMapper.updateById(policy);
    }

    /**
     * 删除政策法规
     */
    @Transactional
    public void deletePolicy(Long id) {
        policyMapper.deleteById(id);
    }

    /**
     * 发布政策法规
     */
    @Transactional
    public void publishPolicy(Long id, Long publisherId) {
        Policy policy = new Policy();
        policy.setId(id);
        policy.setStatus(Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
        policy.setPublisherId(publisherId);
        policy.setPublishTime(LocalDateTime.now());
        policyMapper.updateById(policy);
    }
}
