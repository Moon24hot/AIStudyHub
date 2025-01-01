package com.xuan.service;

import com.xuan.domain.dto.QuestionBankCreateDTO;
import com.xuan.domain.dto.QuestionBankUpdateDTO;
import com.xuan.domain.entity.QuestionBanks;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.domain.vo.QuestionBankVO;
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
public interface IQuestionBanksService extends IService<QuestionBanks> {
    /**
     * 创建题库
     *
     * @param questionBankCreateDTO 题库创建DTO
     * @return 结果
     */
    Result<String> createQuestionBank(QuestionBankCreateDTO questionBankCreateDTO);

    /**
     * 根据用户ID查询题库列表
     *
     * @param userId 用户ID
     * @return 题库列表
     */
    Result<List<QuestionBankVO>> getQuestionBanksByUserId(Integer userId);


    /**
     * 根据题库ID修改题库
     *
     * @param questionBankUpdateDTO 题库更新DTO
     * @return 结果
     */
    Result<String> updateQuestionBank(QuestionBankUpdateDTO questionBankUpdateDTO);

    /**
     * 根据题库ID删除题库
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return 结果
     */
    Result<String> deleteQuestionBank(Integer userId, Integer bankId);

    /**
     * 申请题库公开
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return 结果
     */
    Result<String> applyPublic(Integer userId, Integer bankId);
}
