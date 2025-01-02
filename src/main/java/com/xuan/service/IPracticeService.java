package com.xuan.service;

import com.xuan.domain.dto.UserAnswerDTO;
import com.xuan.domain.vo.QuestionBankDetailVO;
import com.xuan.result.Result;

public interface IPracticeService {
    /**
     * 根据题库ID查询题库详情
     *
     * @param bankId 题库ID
     * @return 题库详情
     */
    Result<QuestionBankDetailVO> getQuestionBankDetail(Integer bankId);

    /**
     * 保存做题进度
     *
     * @param userAnswerDTO 用户答案DTO
     * @return 结果
     */
    Result<String> saveProgress(UserAnswerDTO userAnswerDTO);

    /**
     * 根据用户ID和题库ID清空做题进度
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return 结果
     */
    Result<String> clearProgress(Integer userId, Integer bankId);

    /**
     * 记录错题
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 结果
     */
    Result<String> addWrongQuestion(Integer userId, Integer questionId);

    /**
     * 收藏题目
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 结果
     */
    Result<String> favoriteQuestion(Integer userId, Integer questionId);
}