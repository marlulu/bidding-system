package com.bidding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.common.PageResult;
import com.bidding.entity.BidRecord;
import com.bidding.entity.Announcement;
import com.bidding.mapper.BidMapper;
import com.bidding.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidService {

    @Autowired
    private BidMapper bidMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 提交投标
     */
    @Transactional
    public void submitBid(BidRecord bidRecord) {
        // 1. 检查公告是否存在且在有效期内
        Announcement announcement = announcementMapper.selectById(bidRecord.getAnnouncementId());
        if (announcement == null) {
            throw new RuntimeException("招标公告不存在");
        }
        if (announcement.getBidDeadline() != null && LocalDateTime.now().isAfter(announcement.getBidDeadline())) {
            throw new RuntimeException("投标已截止");
        }

        // 2. 检查供应商是否已经投过标
        LambdaQueryWrapper<BidRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BidRecord::getAnnouncementId, bidRecord.getAnnouncementId());
        queryWrapper.eq(BidRecord::getSupplierId, bidRecord.getSupplierId());
        if (bidMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("您已参与该项目的投标，请勿重复提交");
        }

        // 3. 保存投标记录
        bidRecord.setStatus("SUBMITTED");
        bidRecord.setCreateTime(LocalDateTime.now());
        bidMapper.insert(bidRecord);
    }

    /**
     * 分页查询我的投标
     */
    public PageResult<BidRecord> getMyBids(Integer page, Integer size, Long supplierId) {
        Page<BidRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BidRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BidRecord::getSupplierId, supplierId);
        wrapper.orderByDesc(BidRecord::getCreateTime);

        Page<BidRecord> result = bidMapper.selectPage(pageParam, wrapper);

        // 填充公告标题
        if (!result.getRecords().isEmpty()) {
            for (BidRecord bid : result.getRecords()) {
                Announcement announcement = announcementMapper.selectById(bid.getAnnouncementId());
                if (announcement != null) {
                    bid.setAnnouncementTitle(announcement.getTitle());
                }
            }
        }

        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    /**
     * 管理员查询某个公告的投标列表
     */
    public List<BidRecord> getBidsByAnnouncementId(Long announcementId) {
        LambdaQueryWrapper<BidRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BidRecord::getAnnouncementId, announcementId);
        wrapper.orderByAsc(BidRecord::getBidAmount); // 默认按金额升序
        return bidMapper.selectList(wrapper);
    }

    /**
     * 审核投标（定标）
     */
    @Transactional
    public void auditBid(Long id, String status) {
        BidRecord bid = bidMapper.selectById(id);
        if (bid == null) {
            throw new RuntimeException("投标记录不存在");
        }
        bid.setStatus(status);
        bidMapper.updateById(bid);

        // TODO: 可以发送通知给供应商
    }

    /**
     * 检查供应商是否已投标
     */
    public boolean hasBid(Long announcementId, Long supplierId) {
        LambdaQueryWrapper<BidRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BidRecord::getAnnouncementId, announcementId);
        queryWrapper.eq(BidRecord::getSupplierId, supplierId);
        queryWrapper.eq(BidRecord::getSupplierId, supplierId);
        return bidMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 获取供应商的特定投标记录
     */
    public BidRecord getBidBySupplier(Long announcementId, Long supplierId) {
        LambdaQueryWrapper<BidRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BidRecord::getAnnouncementId, announcementId);
        queryWrapper.eq(BidRecord::getSupplierId, supplierId);
        return bidMapper.selectOne(queryWrapper);
    }
}
