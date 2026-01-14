package com.bidding.controller;

import com.bidding.common.Result;
import com.bidding.service.ExpertExtractionService;
import com.bidding.vo.ExpertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expert-extractions")
public class ExpertExtractionController {

    @Autowired
    private ExpertExtractionService expertExtractionService;

    /**
     * 随机抽取专家
     * @param announcementId 招标公告ID
     * @param extractCount 抽取数量
     * @param extractorId 抽取人ID (当前登录用户ID)
     * @return 抽取的专家列表
     */
    @PostMapping("/extract")
    public Result<List<ExpertVO>> extractExperts(
            @RequestParam Long announcementId,
            @RequestParam Integer extractCount,
            @RequestAttribute("userId") Long extractorId) {
        try {
            List<ExpertVO> experts = expertExtractionService.extractExperts(announcementId, extractCount, extractorId);
            return Result.success("专家抽取成功", experts);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取某个招标公告已抽取的专家列表
     * @param announcementId 招标公告ID
     * @return 已抽取的专家列表
     */
    @GetMapping("/announcement/{announcementId}")
    public Result<List<ExpertVO>> getExtractedExperts(@PathVariable Long announcementId) {
        List<ExpertVO> experts = expertExtractionService.getExtractedExperts(announcementId);
        return Result.success(experts);
    }

    /**
     * 检查某个招标公告是否已抽取过专家
     * @param announcementId 招标公告ID
     * @return 是否已抽取
     */
    @GetMapping("/announcement/{announcementId}/status")
    public Result<Boolean> checkExtractionStatus(@PathVariable Long announcementId) {
        boolean hasExtracted = expertExtractionService.hasExtracted(announcementId);
        return Result.success(hasExtracted);
    }
}
