package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuan.domain.dto.BankReviewDTO;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.AnswerVO;
import com.xuan.domain.vo.BankReviewVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.enums.BanksStatus;
import com.xuan.mapper.*;
import com.xuan.result.Result;
import com.xuan.service.IReviewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Service
public class ReviewsServiceImpl extends ServiceImpl<ReviewsMapper, Reviews> implements IReviewsService {

    @Autowired
    private QuestionBanksMapper questionBanksMapper;

    @Autowired
    private QuestionBankItemsMapper questionBankItemsMapper;

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private AnswersMapper answersMapper;

    /**
     * 获取审核中题库列表
     * @param adminId
     * @return
     */
    @Override
    @Transactional
    public Result<List<BankReviewVO>> getPendingBanks(Integer adminId) {
        // 1. 查询所有状态为“审核中”的题库
        List<QuestionBanks> pendingBanks = questionBanksMapper.selectList(
                new LambdaQueryWrapper<QuestionBanks>()
                        .eq(QuestionBanks::getStatus, BanksStatus.PENDING));

        if (CollectionUtils.isEmpty(pendingBanks)) {
            return Result.success(new ArrayList<>()); // 没有审核中题库，返回空列表
        }

        // 2. 构造 BankReviewVO 列表
        List<BankReviewVO> bankReviewVOList = pendingBanks.stream().map(bank -> {
            BankReviewVO bankReviewVO = new BankReviewVO();
            bankReviewVO.setBankId(bank.getId());
            bankReviewVO.setTitle(bank.getTitle());
            bankReviewVO.setDescription(bank.getDescription());
            bankReviewVO.setStatus(bank.getStatus());

            // 3. 获取题库中的题目ID列表
            List<QuestionBankItems> bankItems = questionBankItemsMapper.selectList(
                    new LambdaQueryWrapper<QuestionBankItems>()
                            .eq(QuestionBankItems::getBankId, bank.getId()));
            List<Integer> questionIds = bankItems.stream()
                    .map(QuestionBankItems::getQuestionId)
                    .collect(Collectors.toList());

            // 4. 查询题目详情
            if (!CollectionUtils.isEmpty(questionIds)) {
                List<Questions> questions = questionsMapper.selectBatchIds(questionIds);

                // 5. 过滤选择题和主观题
                List<QuestionVO> choiceQuestions = new ArrayList<>();
                List<QuestionVO> subjectiveQuestions = new ArrayList<>();

                for (Questions question : questions) {
                    QuestionVO questionVO = new QuestionVO();
                    questionVO.setQuestionId(question.getId());
                    questionVO.setContent(question.getContent());
                    questionVO.setType(question.getType());

                    // 设置答案信息
                    List<Answers> answers = answersMapper.selectList(
                            new LambdaQueryWrapper<Answers>()
                                    .eq(Answers::getQuestionId, question.getId()));
                    List<AnswerVO> answerVOList = answers.stream().map(answer -> {
                        AnswerVO answerVO = new AnswerVO();
                        answerVO.setAnswerId(answer.getId());
                        answerVO.setOptionLabel(answer.getOptionLabel());
                        answerVO.setOptionContent(answer.getOptionContent());
                        answerVO.setIsCorrect(answer.getIsCorrect());
                        answerVO.setSubjectiveAnswer(answer.getSubjectiveAnswer());
                        return answerVO;
                    }).collect(Collectors.toList());
                    questionVO.setAnswers(answerVOList);

                    if ("单选题".equals(question.getType()) || "多选题".equals(question.getType())) {
                        choiceQuestions.add(questionVO);
                    } else if ("主观题".equals(question.getType())) {
                        subjectiveQuestions.add(questionVO);
                    }
                }

                bankReviewVO.setChoiceQuestions(choiceQuestions);
                bankReviewVO.setSubjectiveQuestions(subjectiveQuestions);
            }

            return bankReviewVO;
        }).collect(Collectors.toList());

        return Result.success(bankReviewVOList);
    }

    /**
     * 审核题库
     * @param adminId 管理员ID
     * @param bankReviewDTO 审核信息
     * @return
     */
    @Override
    @Transactional
    public Result<String> reviewBank(Integer adminId, BankReviewDTO bankReviewDTO) {
        // 1. 检查参数
        if (bankReviewDTO.getBankId() == null) {
            return Result.error("题库ID不能为空");
        }
        if (bankReviewDTO.getApproved() == null) {
            return Result.error("审核结果不能为空");
        }

        // 2. 检查题库是否存在以及是否处于待审核状态
        QuestionBanks bank = questionBanksMapper.selectById(bankReviewDTO.getBankId());
        if (bank == null) {
            return Result.error("题库不存在");
        }
        if (bank.getStatus() != BanksStatus.PENDING) {
            return Result.error("题库状态不是待审核");
        }

        // 3. 更新题库状态
        LambdaUpdateWrapper<QuestionBanks> updateWrapper = new LambdaUpdateWrapper<QuestionBanks>()
                .eq(QuestionBanks::getId, bankReviewDTO.getBankId())
                .set(QuestionBanks::getStatus, bankReviewDTO.getApproved() ? BanksStatus.PUBLIC : BanksStatus.REJECTED);
        questionBanksMapper.update(null, updateWrapper);

        return Result.success("题库审核完成");
    }

}
