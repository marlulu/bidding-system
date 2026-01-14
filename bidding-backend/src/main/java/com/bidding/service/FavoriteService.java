package com.bidding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bidding.entity.Favorite;
import com.bidding.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param targetId 收藏目标ID
     * @param targetType 收藏目标类型 (ANNOUNCEMENT, SUPPLIER)
     */
    @Transactional
    public void addFavorite(Long userId, Long targetId, String targetType) {
        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetId, targetId)
                .eq(Favorite::getTargetType, targetType);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已收藏");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetId(targetId);
        favorite.setTargetType(targetType);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param targetId 收藏目标ID
     * @param targetType 收藏目标类型
     */
    @Transactional
    public void removeFavorite(Long userId, Long targetId, String targetType) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetId, targetId)
                .eq(Favorite::getTargetType, targetType);
        favoriteMapper.delete(wrapper);
    }

    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param targetType 收藏目标类型（可选）
     * @return 收藏列表
     */
    public Page<Favorite> getFavoritesByUserId(Long userId, Integer pageNum, Integer pageSize, String targetType) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        if (targetType != null) {
            wrapper.eq(Favorite::getTargetType, targetType);
        }
        wrapper.orderByDesc(Favorite::getCreateTime);
        return favoriteMapper.selectPage(page, wrapper);
    }

    /**
     * 检查用户是否已收藏某个目标
     * @param userId 用户ID
     * @param targetId 收藏目标ID
     * @param targetType 收藏目标类型
     * @return 是否已收藏
     */
    public boolean isFavorited(Long userId, Long targetId, String targetType) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetId, targetId)
                .eq(Favorite::getTargetType, targetType);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}
