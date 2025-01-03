package com.xuan.service;

import com.xuan.domain.entity.Favorites;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.result.Result;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
public interface IFavoritesService extends IService<Favorites> {

    /**
     * 获取用户收藏的所有题目列表（包括收藏的题库中的题目）
     * @param userId 用户ID
     * @return 题目列表
     */
    Result<List<QuestionVO>> getAllFavoriteQuestions(Integer userId);

}
