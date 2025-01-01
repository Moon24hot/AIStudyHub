package com.xuan.service;

import com.xuan.domain.entity.WrongQuestions;
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
public interface IWrongQuestionsService extends IService<WrongQuestions> {

    /**
     * 获取用户错题列表
     * @param userId 用户ID
     * @return 错题列表
     */
    Result<List<QuestionVO>> getWrongQuestions(Integer userId);

}
