package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Policy;
import com.bidding.service.PolicyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * 获取政策法规列表
     */
    @GetMapping
    public Result<PageResult<Policy>> getPolicyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            
            PageResult<Policy> result = policyService.getPolicyList(page, size, keyword, category, status, role);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取政策法规详情
     */
    @GetMapping("/{id}")
    public Result<Policy> getPolicyById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            
            Policy policy = policyService.getPolicyById(id, role);
            return Result.success(policy);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建政策法规
     */
    @PostMapping
    public Result<Void> createPolicy(@RequestBody Policy policy) {
        try {
            policyService.createPolicy(policy);
            return Result.success("创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新政策法规
     */
    @PutMapping("/{id}")
    public Result<Void> updatePolicy(@PathVariable Long id, @RequestBody Policy policy) {
        try {
            policy.setId(id);
            policyService.updatePolicy(policy);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除政策法规
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePolicy(@PathVariable Long id) {
        try {
            policyService.deletePolicy(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布政策法规
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishPolicy(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long publisherId = (Long) request.getAttribute("userId");
            policyService.publishPolicy(id, publisherId);
            return Result.success("发布成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
