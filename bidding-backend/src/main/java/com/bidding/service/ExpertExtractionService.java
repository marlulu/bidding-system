package com.bidding.service;

import com.bidding.entity.ExpertExtraction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bidding.vo.ExpertVO;

import java.util.List;

public interface ExpertExtractionService extends IService<ExpertExtraction> {

    /**
     * 随机抽取专家
     * @param announcementId 招标公告ID
     * @param extractCount 抽取数量
     * @param extractorId 抽取人ID
     * @return 抽取的专家列表
     */
    List<ExpertVO> extractExperts(Long announcementId, Integer extractCount, Long extractorId);

    /**
     * 获取某个招标公告已抽取的专家列表
     * @param announcementId 招标公告ID
     * @return 已抽取的专家列表
     */
    List<ExpertVO> getExtractedExperts(Long announcementId);

    /**
     * 检查某个招标公告是否已抽取过专家
     * @param announcementId 招标公告ID
     * @return 是否已抽取
     */
    boolean hasExtracted(Long announcementId);
}
