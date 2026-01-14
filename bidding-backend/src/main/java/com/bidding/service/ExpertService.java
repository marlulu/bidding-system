package com.bidding.service;

import com.bidding.entity.Expert;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ExpertService extends IService<Expert> {

    /**
     * 分页查询专家列表
     * @param page 分页对象
     * @param expert 查询条件
     * @return 专家分页列表
     */
    IPage<Expert> getExpertPage(Page<Expert> page, Expert expert);

    /**
     * 新增专家
     * @param expert 专家信息
     * @return 是否成功
     */
    boolean addExpert(Expert expert);

    /**
     * 更新专家
     * @param expert 专家信息
     * @return 是否成功
     */
    boolean updateExpert(Expert expert);

    /**
     * 删除专家
     * @param id 专家ID
     * @return 是否成功
     */
    boolean deleteExpert(Long id);
}
