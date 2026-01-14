package com.bidding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.PageResult;
import com.bidding.entity.Supplier;
import com.bidding.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    /**
     * 获取供应商列表
     */
    public PageResult<Supplier> getSupplierList(Integer page, Integer size, String keyword, 
                                                  String industry, String scale, Integer status) {
        Page<Supplier> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Supplier::getCompanyName, keyword)
                    .or().like(Supplier::getCompanyCode, keyword)
                    .or().like(Supplier::getContactName, keyword));
        }
        
        if (StringUtils.hasText(industry)) {
            wrapper.eq(Supplier::getIndustry, industry);
        }
        
        if (StringUtils.hasText(scale)) {
            wrapper.eq(Supplier::getScale, scale);
        }
        
        if (status != null) {
            wrapper.eq(Supplier::getStatus, status);
        }
        
        wrapper.orderByDesc(Supplier::getCreateTime);
        Page<Supplier> result = supplierMapper.selectPage(pageParam, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    /**
     * 获取供应商详情
     */
    public Supplier getSupplierById(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new RuntimeException("供应商不存在");
        }
        return supplier;
    }

    /**
     * 创建供应商
     */
    @Transactional
    public void createSupplier(Supplier supplier) {
        // 检查统一社会信用代码是否已存在
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getCompanyCode, supplier.getCompanyCode());
        if (supplierMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("统一社会信用代码已存在");
        }
        supplier.setStatus(0); // 新创建的供应商默认为待审核状态
        supplierMapper.insert(supplier);
    }

    /**
     * 更新供应商
     */
    @Transactional
    public void updateSupplier(Supplier supplier) {
        Supplier existSupplier = supplierMapper.selectById(supplier.getId());
        if (existSupplier == null) {
            throw new RuntimeException("供应商不存在");
        }

        supplierMapper.updateById(supplier);
    }

    /**
     * 删除供应商
     */
    @Transactional
    public void deleteSupplier(Long id) {
        supplierMapper.deleteById(id);
    }

    /**
     * 更新供应商状态
     */
    @Transactional
    public void updateSupplierStatus(Long id, Integer status) {
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setStatus(status);
        supplierMapper.updateById(supplier);
    }

    /**
     * 审核供应商
     */
    @Transactional
    public void auditSupplier(Long id, Integer status, String auditRemark, Long auditorId) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new RuntimeException("供应商不存在");
        }
        supplier.setStatus(status);
        supplier.setAuditRemark(auditRemark);
        supplier.setAuditorId(auditorId);
        supplierMapper.updateById(supplier);
    }

    /**
     * 获取所有供应商（用于下拉选择）
     */
    public List<Supplier> getAllSuppliers() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, 1); // 只返回已认证的供应商
        wrapper.orderByDesc(Supplier::getCreateTime);
        return supplierMapper.selectList(wrapper);
    }
}
