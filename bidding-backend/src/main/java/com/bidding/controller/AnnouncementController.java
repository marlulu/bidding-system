package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Announcement;
import com.bidding.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/announcements") // 移除 /api，因为 context-path 已配置为 /api
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public Result<PageResult<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String industry,
            HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");

            PageResult<Announcement> result = announcementService.getAnnouncementList(
                    page, size, keyword, type, status, role, supplierId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");

            Announcement announcement = announcementService.getAnnouncementById(id, role, supplierId);
            return Result.success(announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            throw e;
        }
    }

    @PostMapping
    public Result<Void> createAnnouncement(@RequestBody Announcement announcement) {
        try {
            announcementService.createAnnouncement(announcement);
            return Result.success("创建成功", null);
        } catch (Exception e) {
            log.error("创建公告失败", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement,
            HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("userId");
            String currentUserRole = (String) request.getAttribute("role");
            announcement.setId(id);
            announcementService.updateAnnouncement(announcement, currentUserId, currentUserRole);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新公告失败", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除公告失败", e);
            throw e;
        }
    }

    @PutMapping("/{id}/publish")
    public Result<Void> publishAnnouncement(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long publisherId = (Long) request.getAttribute("userId");
            announcementService.publishAnnouncement(id, publisherId);
            return Result.success("发布成功", null);
        } catch (Exception e) {
            log.error("发布公告失败", e);
            throw e;
        }
    }
}
