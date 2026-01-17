package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Expert;
import com.bidding.service.ExpertService;
import com.bidding.vo.ExpertVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/experts")
public class ExpertController {

    @Autowired
    private ExpertService expertService;

    /**
     * 分页查询专家列表
     */
    @GetMapping
    public Result<PageResult<ExpertVO>> getExpertPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Expert expert) {
        Page<Expert> pageParam = new Page<>(page, size);
        IPage<Expert> expertPage = expertService.getExpertPage(pageParam, expert);
        
        List<ExpertVO> voList = expertPage.getRecords().stream().map(e -> {
            ExpertVO vo = new ExpertVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(new PageResult<>(expertPage.getTotal(), voList));
    }

    /**
     * 根据ID查询专家
     */
    @GetMapping("/{id}")
    public Result<Expert> getExpertById(@PathVariable Long id) {
        Expert expert = expertService.getById(id);
        return expert != null ? Result.success(expert) : Result.error("专家不存在");
    }

    /**
     * 新增专家
     */
    @PostMapping
    public Result<Void> addExpert(@Valid @RequestBody Expert expert) {
        boolean success = expertService.addExpert(expert);
        return success ? Result.success("新增专家成功", null) : Result.error("新增专家失败");
    }

    /**
     * 更新专家
     */
    @PutMapping("/{id}")
    public Result<Void> updateExpert(@PathVariable Long id, @Valid @RequestBody Expert expert) {
        expert.setId(id);
        boolean success = expertService.updateExpert(expert);
        return success ? Result.success("更新专家成功", null) : Result.error("更新专家失败");
    }

    /**
     * 删除专家
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteExpert(@PathVariable Long id) {
        boolean success = expertService.deleteExpert(id);
        return success ? Result.success("删除专家成功", null) : Result.error("删除专家失败");
    }
}
