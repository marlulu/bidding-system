package com.bidding.service.impl;

import com.bidding.entity.Expert;
import com.bidding.entity.ExpertExtraction;
import com.bidding.mapper.ExpertExtractionMapper;
import com.bidding.mapper.ExpertMapper;
import com.bidding.service.ExpertExtractionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bidding.vo.ExpertVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpertExtractionServiceImpl extends ServiceImpl<ExpertExtractionMapper, ExpertExtraction> implements ExpertExtractionService {

    @Autowired
    private ExpertMapper expertMapper;

    @Override
    @Transactional
    public List<ExpertVO> extractExperts(Long announcementId, Integer extractCount, Long extractorId) {
        // 检查是否已抽取过
        if (hasExtracted(announcementId)) {
            throw new RuntimeException("该招标公告已抽取过专家，请勿重复抽取。");
        }

        // 获取所有未删除的专家
        QueryWrapper<Expert> expertQueryWrapper = new QueryWrapper<>();
        expertQueryWrapper.eq("deleted", 0);
        List<Expert> allExperts = expertMapper.selectList(expertQueryWrapper);

        if (allExperts.size() < extractCount) {
            throw new RuntimeException("专家库数量不足，无法抽取指定数量的专家。");
        }

        // 随机打乱专家列表
        Collections.shuffle(allExperts);

        // 抽取指定数量的专家
        List<Expert> extractedExperts = allExperts.subList(0, extractCount);

        // 保存抽取记录
        for (Expert expert : extractedExperts) {
            ExpertExtraction extraction = new ExpertExtraction();
            extraction.setAnnouncementId(announcementId);
            extraction.setExpertId(expert.getId());
            extraction.setExtractTime(LocalDateTime.now());
            extraction.setExtractorId(extractorId);
            extraction.setDeleted(0);
            baseMapper.insert(extraction);
        }

        // 转换为VO返回
        return extractedExperts.stream().map(expert -> {
            ExpertVO expertVO = new ExpertVO();
            BeanUtils.copyProperties(expert, expertVO);
            return expertVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ExpertVO> getExtractedExperts(Long announcementId) {
        QueryWrapper<ExpertExtraction> extractionQueryWrapper = new QueryWrapper<>();
        extractionQueryWrapper.eq("announcement_id", announcementId);
        extractionQueryWrapper.eq("deleted", 0);
        List<ExpertExtraction> extractions = baseMapper.selectList(extractionQueryWrapper);

        if (extractions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> expertIds = extractions.stream()
                .map(ExpertExtraction::getExpertId)
                .collect(Collectors.toList());

        QueryWrapper<Expert> expertQueryWrapper = new QueryWrapper<>();
        expertQueryWrapper.in("id", expertIds);
        expertQueryWrapper.eq("deleted", 0);
        List<Expert> experts = expertMapper.selectList(expertQueryWrapper);

        return experts.stream().map(expert -> {
            ExpertVO expertVO = new ExpertVO();
            BeanUtils.copyProperties(expert, expertVO);
            return expertVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean hasExtracted(Long announcementId) {
        QueryWrapper<ExpertExtraction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("announcement_id", announcementId);
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectCount(queryWrapper) > 0;
    }
}
