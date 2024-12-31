package com.xuan.service;

import com.xuan.domain.dto.QuestionAddDTO;
import com.xuan.domain.dto.QuestionUpdateDTO;
import com.xuan.domain.entity.Questions;
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
public interface IQuestionsService extends IService<Questions> {
    /**
     * 新增题目
     * @param questionAddDTO
     * @return
     */
    Result<String> addQuestion(QuestionAddDTO questionAddDTO);

    /**
     * 根据用户ID查询题目列表（包括用户创建的和收藏的题库中的题目）
     *
     * @param userId 用户ID
     * @return 题目列表
     */
    Result<List<QuestionVO>> getQuestionsByUserId(Integer userId);

    /**
     * 根据题目ID修改题目
     *
     * @param questionUpdateDTO 题目更新DTO
     * @return 结果
     */
    Result<String> updateQuestion(QuestionUpdateDTO questionUpdateDTO);

    /**
     * 根据题目ID删除题目
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 结果
     */
    Result<String> deleteQuestion(Integer userId, Integer questionId);


}
