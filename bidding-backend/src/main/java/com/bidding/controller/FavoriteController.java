package com.bidding.controller;

import com.bidding.common.PageResult;
import com.bidding.common.Result;
import com.bidding.entity.Favorite;
import com.bidding.service.FavoriteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     * @param favorite 收藏信息 (userId, targetId, targetType)
     * @return 结果
     */
    @PostMapping
    public Result<String> addFavorite(@RequestBody Favorite favorite) {
        Long userId = (Long) this.request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
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
        Long userId = (Long) this.request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        favoriteService.removeFavorite(userId, targetId, targetType);
        return Result.success("取消收藏成功");
    }

    /**
     * 获取用户收藏列表
     * @param page 页码
     * @param size 每页数量
     * @param targetType 收藏目标类型（可选）
     * @return 收藏列表
     */
    @GetMapping("/list")
    public Result<PageResult<Favorite>> getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String targetType) {
        Long userId = (Long) this.request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        Page<Favorite> favorites = favoriteService.getFavoritesByUserId(userId, page, size, targetType);
        return Result.success(new PageResult<>(favorites.getTotal(), favorites.getRecords()));
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
        Long userId = (Long) this.request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        boolean isFavorited = favoriteService.isFavorited(userId, targetId, targetType);
        return Result.success(isFavorited);
    }
}
