package com.bidding.controller;

import com.bidding.common.Result;
import com.bidding.entity.Expert;
import com.bidding.service.ExpertService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/experts")
public class ExpertController {

    @Autowired
    private ExpertService expertService;

    /**
     * 分页查询专家列表
     */
    @GetMapping
    public Result<IPage<Expert>> getExpertPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Expert expert) {
        Page<Expert> page = new Page<>(pageNum, pageSize);
        IPage<Expert> expertPage = expertService.getExpertPage(page, expert);
        return Result.success(expertPage);
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
