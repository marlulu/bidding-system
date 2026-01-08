package com.bidding.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.Constants;
import com.bidding.common.PageResult;
import com.bidding.entity.Announcement;
import com.bidding.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    public PageResult<Announcement> getAnnouncementList(Integer page, Integer size, String keyword, 
                                                         String type, String status, 
                                                         String role, Long supplierId) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            wrapper.eq(Announcement::getStatus, Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
            wrapper.and(w -> w.eq(Announcement::getVisibilityLevel, Constants.VISIBILITY_PUBLIC)
                    .or(ow -> ow.eq(Announcement::getVisibilityLevel, Constants.VISIBILITY_RESTRICTED)
                            .and(iw -> iw.apply("JSON_CONTAINS(visible_supplier_ids, CAST({0} AS JSON))", supplierId))));
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Announcement::getTitle, keyword)
                    .or().like(Announcement::getAnnouncementNo, keyword)
                    .or().like(Announcement::getProjectName, keyword));
        }
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(Announcement::getType, type);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Announcement::getStatus, status);
        }
        
        // 增加置顶排序
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getPublishTime)
               .orderByDesc(Announcement::getCreateTime);
               
        Page<Announcement> result = announcementMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public Announcement getAnnouncementById(Long id, String role, Long supplierId) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new RuntimeException("招标公告不存在");
        }
        if (Constants.ROLE_SUPPLIER.equals(role)) {
            if (!Constants.ANNOUNCEMENT_STATUS_PUBLISHED.equals(announcement.getStatus())) {
                throw new RuntimeException("无权查看该公告");
            }
            if (Constants.VISIBILITY_RESTRICTED.equals(announcement.getVisibilityLevel())) {
                List<Long> visibleIds = JSON.parseArray(announcement.getVisibleSupplierIds(), Long.class);
                if (visibleIds == null || !visibleIds.contains(supplierId)) {
                    throw new RuntimeException("无权查看该公告");
                }
            }
        }
        return announcement;
    }

    @Transactional
    public void createAnnouncement(Announcement announcement) {
        announcement.setStatus(Constants.ANNOUNCEMENT_STATUS_DRAFT);
        announcementMapper.insert(announcement);
    }

    @Transactional
    public void updateAnnouncement(Announcement announcement) {
        announcementMapper.updateById(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    @Transactional
    public void publishAnnouncement(Long id, Long publisherId) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(Constants.ANNOUNCEMENT_STATUS_PUBLISHED);
        announcement.setPublisherId(publisherId);
        announcement.setPublishTime(LocalDateTime.now());
        announcementMapper.updateById(announcement);
    }
}
