package com.bidding.service.impl;

import com.bidding.entity.Expert;
import com.bidding.mapper.ExpertMapper;
import com.bidding.service.ExpertService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements ExpertService {

    @Override
    public IPage<Expert> getExpertPage(Page<Expert> page, Expert expert) {
        QueryWrapper<Expert> queryWrapper = new QueryWrapper<>();
        if (expert.getName() != null && !expert.getName().isEmpty()) {
            queryWrapper.like("name", expert.getName());
        }
        if (expert.getSpecialty() != null && !expert.getSpecialty().isEmpty()) {
            queryWrapper.like("specialty", expert.getSpecialty());
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public boolean addExpert(Expert expert) {
        expert.setCreateTime(LocalDateTime.now());
        expert.setUpdateTime(LocalDateTime.now());
        expert.setDeleted(0);
        return save(expert);
    }

    @Override
    public boolean updateExpert(Expert expert) {
        expert.setUpdateTime(LocalDateTime.now());
        return updateById(expert);
    }

    @Override
    public boolean deleteExpert(Long id) {
        Expert expert = new Expert();
        expert.setId(id);
        expert.setDeleted(1);
        expert.setUpdateTime(LocalDateTime.now());
        return updateById(expert);
    }
}
