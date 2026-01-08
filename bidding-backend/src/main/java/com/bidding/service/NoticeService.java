package com.bidding.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.Constants;
import com.bidding.common.PageResult;
import com.bidding.entity.Notice;
import com.bidding.entity.NoticeReadRecord;
import com.bidding.mapper.NoticeMapper;
import com.bidding.mapper.NoticeReadRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeReadRecordMapper noticeReadRecordMapper;

    /**
     * 获取系统通知列表（根据角色和权限过滤）
     */
    public PageResult<Notice> getNoticeList(Integer page, Integer size, String keyword, 
                                             String type, String status, 
                                             String role, Long supplierId) {
        Page<Notice> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        // 供应商只能查看已发布的通知
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            wrapper.eq(Notice::getStatus, Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
            
            // 根据目标类型过滤
            wrapper.and(w -> w.eq(Notice::getTargetType, Constants.NOTICE_TARGET_ALL)
                    .or(ow -> ow.eq(Notice::getTargetType, Constants.NOTICE_TARGET_SUPPLIER)
                            .and(iw -> iw.apply("JSON_CONTAINS(target_supplier_ids, CAST({0} AS JSON))", supplierId))));
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Notice::getTitle, keyword)
                    .or().like(Notice::getContent, keyword));
        }
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(Notice::getType, type);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Notice::getStatus, status);
        }
        
        wrapper.orderByDesc(Notice::getCreateTime);
        Page<Notice> result = noticeMapper.selectPage(pageParam, wrapper);
        
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    /**
     * 获取系统通知详情
     */
    public Notice getNoticeById(Long id, String role, Long supplierId) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("系统通知不存在");
        }
        
        // 供应商权限检查
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            if (!Constants.ANNOUNCEMENT_STATUS_PUBLISHED.equals(notice.getStatus())) {
                throw new RuntimeException("无权查看该通知");
            }
            
            // 检查目标类型
            if (Constants.NOTICE_TARGET_SUPPLIER.equals(notice.getTargetType())) {
                List<Long> targetIds = JSON.parseArray(notice.getTargetSupplierIds(), Long.class);
                if (targetIds == null || !targetIds.contains(supplierId)) {
                    throw new RuntimeException("无权查看该通知");
                }
            }
        }
        
        return notice;
    }

    /**
     * 创建系统通知
     */
    @Transactional
    public void createNotice(Notice notice) {
        notice.setStatus(Constants.ANNOUNCEMENT_STATUS_DRAFT);
        noticeMapper.insert(notice);
    }

    /**
     * 更新系统通知
     */
    @Transactional
    public void updateNotice(Notice notice) {
        Notice existNotice = noticeMapper.selectById(notice.getId());
        if (existNotice == null) {
            throw new RuntimeException("系统通知不存在");
        }

        noticeMapper.updateById(notice);
    }

    /**
     * 删除系统通知
     */
    @Transactional
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
    }

    /**
     * 发布系统通知
     */
    @Transactional
    public void publishNotice(Long id, Long publisherId) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setStatus(Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
        notice.setPublisherId(publisherId);
        notice.setPublishTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long noticeId, Long userId) {
        // 检查是否已读
        LambdaQueryWrapper<NoticeReadRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeReadRecord::getNoticeId, noticeId)
               .eq(NoticeReadRecord::getUserId, userId);
        
        if (noticeReadRecordMapper.selectCount(wrapper) == 0) {
            NoticeReadRecord record = new NoticeReadRecord();
            record.setNoticeId(noticeId);
            record.setUserId(userId);
            record.setReadTime(LocalDateTime.now());
            noticeReadRecordMapper.insert(record);
        }
    }

    /**
     * 获取未读通知数量
     */
    public Long getUnreadCount(Long userId, String role, Long supplierId) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
        
        // 供应商只能看到针对自己的通知
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            wrapper.and(w -> w.eq(Notice::getTargetType, Constants.NOTICE_TARGET_ALL)
                    .or(ow -> ow.eq(Notice::getTargetType, Constants.NOTICE_TARGET_SUPPLIER)
                            .and(iw -> iw.apply("JSON_CONTAINS(target_supplier_ids, CAST({0} AS JSON))", supplierId))));
        }
        
        List<Notice> notices = noticeMapper.selectList(wrapper);
        
        // 过滤掉已读的通知
        long unreadCount = 0;
        for (Notice notice : notices) {
            LambdaQueryWrapper<NoticeReadRecord> readWrapper = new LambdaQueryWrapper<>();
            readWrapper.eq(NoticeReadRecord::getNoticeId, notice.getId())
                       .eq(NoticeReadRecord::getUserId, userId);
            if (noticeReadRecordMapper.selectCount(readWrapper) == 0) {
                unreadCount++;
            }
        }
        
        return unreadCount;
    }
}
