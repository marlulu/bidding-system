package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Announcement;
import com.bidding.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 获取招标公告列表
     */
    @GetMapping
    public Result<PageResult<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");
            
            PageResult<Announcement> result = announcementService.getAnnouncementList(
                    page, size, keyword, type, status, role, supplierId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取招标公告详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");
            
            Announcement announcement = announcementService.getAnnouncementById(id, role, supplierId);
            return Result.success(announcement);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建招标公告
     */
    @PostMapping
    public Result<Void> createAnnouncement(@RequestBody Announcement announcement) {
        try {
            announcementService.createAnnouncement(announcement);
            return Result.success("创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新招标公告
     */
    @PutMapping("/{id}")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        try {
            announcement.setId(id);
            announcementService.updateAnnouncement(announcement);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除招标公告
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布招标公告
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishAnnouncement(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long publisherId = (Long) request.getAttribute("userId");
            announcementService.publishAnnouncement(id, publisherId);
            return Result.success("发布成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
