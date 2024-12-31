package com.xuan.service;

import com.xuan.domain.dto.QuestionAddDTO;
import com.xuan.domain.entity.Questions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.result.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
public interface IQuestionsService extends IService<Questions> {
    /**
     * 新增题目
     * @param questionAddDTO
     * @return
     */
    Result<String> addQuestion(QuestionAddDTO questionAddDTO);
}
