package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Supplier;
import com.bidding.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.bidding.common.Constants;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private void checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (role == null || !Constants.ROLE_ADMIN.equals(role)) {
            throw new RuntimeException("无权操作，仅限管理员");
        }
    }


    @Autowired
    private SupplierService supplierService;

    /**
     * 获取供应商列表
     */
    @GetMapping
    public Result<PageResult<Supplier>> getSupplierList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String scale,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<Supplier> result = supplierService.getSupplierList(page, size, keyword, industry, scale, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有供应商（用于下拉选择）
     */
    @GetMapping("/all")
    public Result<List<Supplier>> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            return Result.success(suppliers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取供应商详情
     */
    @GetMapping("/{id}")
    public Result<Supplier> getSupplierById(@PathVariable Long id) {
        try {
            Supplier supplier = supplierService.getSupplierById(id);
            return Result.success(supplier);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建供应商
     */
    @PostMapping
    public Result<Void> createSupplier(@RequestBody Supplier supplier, HttpServletRequest request) {
        checkAdmin(request);
        try {
            supplierService.createSupplier(supplier);
            return Result.success("创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 供应商注册
     */
    @PostMapping("/register")
    public Result<Void> registerSupplier(@RequestBody Supplier supplier) {
        try {
            supplierService.createSupplier(supplier);
            return Result.success("注册成功，请等待审核", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新供应商
     */
    @PutMapping("/{id}")
    public Result<Void> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier, HttpServletRequest request) {
        checkAdmin(request);
        try {
            supplier.setId(id);
            supplierService.updateSupplier(supplier);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSupplier(@PathVariable Long id, HttpServletRequest request) {
        checkAdmin(request);
        try {
            supplierService.deleteSupplier(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新供应商状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateSupplierStatus(@PathVariable Long id, @RequestBody Supplier supplier, HttpServletRequest request) {
        checkAdmin(request);
        try {
            supplierService.updateSupplierStatus(id, supplier.getStatus());
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核供应商
     */
    @PutMapping("/{id}/audit")
    public Result<Void> auditSupplier(@PathVariable Long id, @RequestBody Supplier supplier, HttpServletRequest request) {
        checkAdmin(request);
        try {
            Long auditorId = (Long) request.getAttribute("userId");
            supplierService.auditSupplier(id, supplier.getStatus(), supplier.getAuditRemark(), auditorId);
            return Result.success("审核成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
