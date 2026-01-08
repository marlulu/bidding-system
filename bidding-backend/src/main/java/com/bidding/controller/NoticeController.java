package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Notice;
import com.bidding.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取系统通知列表
     */
    @GetMapping
    public Result<PageResult<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");
            
            PageResult<Notice> result = noticeService.getNoticeList(page, size, keyword, type, status, role, supplierId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取系统通知详情
     */
    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");
            
            Notice notice = noticeService.getNoticeById(id, role, supplierId);
            return Result.success(notice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建系统通知
     */
    @PostMapping
    public Result<Void> createNotice(@RequestBody Notice notice) {
        try {
            noticeService.createNotice(notice);
            return Result.success("创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新系统通知
     */
    @PutMapping("/{id}")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        try {
            notice.setId(id);
            noticeService.updateNotice(notice);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除系统通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布系统通知
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishNotice(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long publisherId = (Long) request.getAttribute("userId");
            noticeService.publishNotice(id, publisherId);
            return Result.success("发布成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            noticeService.markAsRead(id, userId);
            return Result.success("标记成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long supplierId = (Long) request.getAttribute("supplierId");
            
            Long count = noticeService.getUnreadCount(userId, role, supplierId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
