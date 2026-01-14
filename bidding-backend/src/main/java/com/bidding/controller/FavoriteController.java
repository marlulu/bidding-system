package com.bidding.controller;

import com.bidding.common.Result;
import com.bidding.entity.Favorite;
import com.bidding.service.FavoriteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     * @param favorite 收藏信息 (userId, targetId, targetType)
     * @return 结果
     */
    @PostMapping
    public Result<String> addFavorite(@RequestBody Favorite favorite) {
        // TODO: userId 应该从当前登录用户中获取
        Long userId = 1L; // 示例：假设当前用户ID为1
        favoriteService.addFavorite(userId, favorite.getTargetId(), favorite.getTargetType());
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     * @param targetId 收藏目标ID
     * @param targetType 收藏目标类型
     * @return 结果
     */
    @DeleteMapping
    public Result<String> removeFavorite(
            @RequestParam Long targetId,
            @RequestParam String targetType) {
        // TODO: userId 应该从当前登录用户中获取
        Long userId = 1L; // 示例：假设当前用户ID为1
        favoriteService.removeFavorite(userId, targetId, targetType);
        return Result.success("取消收藏成功");
    }

    /**
     * 获取用户收藏列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param targetType 收藏目标类型（可选）
     * @return 收藏列表
     */
    @GetMapping
    public Result<Page<Favorite>> getFavorites(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String targetType) {
        // TODO: userId 应该从当前登录用户中获取
        Long userId = 1L; // 示例：假设当前用户ID为1
        Page<Favorite> favorites = favoriteService.getFavoritesByUserId(userId, pageNum, pageSize, targetType);
        return Result.success(favorites);
    }

    /**
     * 检查用户是否已收藏某个目标
     * @param targetId 收藏目标ID
     * @param targetType 收藏目标类型
     * @return 是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(
            @RequestParam Long targetId,
            @RequestParam String targetType) {
        // TODO: userId 应该从当前登录用户中获取
        Long userId = 1L; // 示例：假设当前用户ID为1
        boolean isFavorited = favoriteService.isFavorited(userId, targetId, targetType);
        return Result.success(isFavorited);
    }
}
