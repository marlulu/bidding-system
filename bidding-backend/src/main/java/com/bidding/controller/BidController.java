package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.BidRecord;
import com.bidding.service.BidService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    /**
     * 提交投标
     */
    @PostMapping
    public Result<Void> submitBid(@RequestBody BidRecord bidRecord, HttpServletRequest request) {
        try {
            Long supplierId = (Long) request.getAttribute("supplierId");
            if (supplierId == null) {
                return Result.error("非供应商用户无法投标");
            }
            bidRecord.setSupplierId(supplierId);
            bidService.submitBid(bidRecord);
            return Result.success("投标成功", null);
        } catch (Exception e) {
            log.error("投标失败", e);
            throw e; // 全局异常处理器会捕获
        }
    }

    /**
     * 供应商查询自己的投标记录
     */
    @GetMapping("/my")
    public Result<PageResult<BidRecord>> getMyBids(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long supplierId = (Long) request.getAttribute("supplierId");
        if (supplierId == null) {
            return Result.error("非供应商用户无法查看");
        }
        PageResult<BidRecord> result = bidService.getMyBids(page, size, supplierId);
        return Result.success(result);
    }

    /**
     * 管理员查询某个公告的投标列表
     */
    @GetMapping("/announcement/{announcementId}")
    public Result<List<BidRecord>> getBidsByAnnouncement(@PathVariable Long announcementId) {
        List<BidRecord> list = bidService.getBidsByAnnouncementId(announcementId);
        return Result.success(list);
    }

    /**
     * 审核/定标
     */
    @PutMapping("/{id}/audit")
    public Result<Void> auditBid(@PathVariable Long id, @RequestBody BidRecord bidRecord) {
        try {
            bidService.auditBid(id, bidRecord.getStatus());
            return Result.success("操作成功", null);
        } catch (Exception e) {
            log.error("定标失败", e);
            throw e;
        }
    }

    /**
     * 检查当前用户是否已投标某公告
     */
    @GetMapping("/check/{announcementId}")
    public Result<Boolean> checkHasBid(@PathVariable Long announcementId, HttpServletRequest request) {
        Long supplierId = (Long) request.getAttribute("supplierId");
        if (supplierId == null) {
            return Result.success(false);
        }
        boolean hasBid = bidService.hasBid(announcementId, supplierId);
        return Result.success(hasBid);
    }

    /**
     * 获取当前用户在某公告下的投标详情
     */
    @GetMapping("/detail/{announcementId}")
    public Result<BidRecord> getMyBidDetail(@PathVariable Long announcementId, HttpServletRequest request) {
        Long supplierId = (Long) request.getAttribute("supplierId");
        if (supplierId == null) {
            return Result.error("非供应商用户");
        }
        BidRecord bid = bidService.getBidBySupplier(announcementId, supplierId);
        return Result.success(bid);
    }
}
