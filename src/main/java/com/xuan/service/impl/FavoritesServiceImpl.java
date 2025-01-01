package com.xuan.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.domain.entity.Favorites;
import com.xuan.mapper.*;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.AnswerVO;
import com.xuan.domain.vo.QuestionBankVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.result.Result;
import com.xuan.service.IFavoritesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements IFavoritesService {

    @Autowired
    private FavoritesMapper favoritesMapper;

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private AnswersMapper answersMapper;

    @Autowired
    private QuestionBanksMapper questionBanksMapper;

    @Autowired
    private QuestionBankItemsMapper questionBankItemsMapper;

    @Override
    public Result<List<QuestionVO>> getFavoriteQuestions(Integer userId) {
        // 1. 查询用户收藏的题目ID列表
        List<Favorites> favorites = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题目"));

        if (CollectionUtils.isEmpty(favorites)) {
            return Result.success(new ArrayList<>()); // 没有收藏的题目，返回空列表
        }

        List<Integer> questionIds = favorites.stream()
                .map(Favorites::getItemId)
                .collect(Collectors.toList());

        // 2. 查询题目详情
        List<Questions> questions = questionsMapper.selectBatchIds(questionIds);

        // 3. 构造 QuestionVO 列表
        List<QuestionVO> questionVOList = questions.stream().map(question -> {
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

            return questionVO;
        }).collect(Collectors.toList());

        return Result.success(questionVOList);
    }

    @Override
    public Result<List<QuestionBankVO>> getFavoriteQuestionBanks(Integer userId) {
        // 1. 查询用户收藏的题库ID列表
        List<Favorites> favorites = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题库"));

        if (CollectionUtils.isEmpty(favorites)) {
            return Result.success(new ArrayList<>()); // 没有收藏的题库，返回空列表
        }

        List<Integer> bankIds = favorites.stream()
                .map(Favorites::getItemId)
                .collect(Collectors.toList());

        // 2. 查询题库详情
        List<QuestionBanks> questionBanks = questionBanksMapper.selectBatchIds(bankIds);

        // 3. 构造 QuestionBankVO 列表
        List<QuestionBankVO> questionBankVOList = questionBanks.stream().map(bank -> {
            QuestionBankVO questionBankVO = new QuestionBankVO();
            questionBankVO.setBankId(bank.getId());
            questionBankVO.setTitle(bank.getTitle());
            questionBankVO.setDescription(bank.getDescription());

            // 4. 获取题库中的题目ID列表
            List<QuestionBankItems> bankItems = questionBankItemsMapper.selectList(
                    new LambdaQueryWrapper<QuestionBankItems>()
                            .eq(QuestionBankItems::getBankId, bank.getId()));
            List<Integer> questionIds = bankItems.stream()
                    .map(QuestionBankItems::getQuestionId)
                    .collect(Collectors.toList());

            // 5. 查询题目详情
            if (!CollectionUtils.isEmpty(questionIds)) {
                List<Questions> questions = questionsMapper.selectBatchIds(questionIds);

                // 6. 构造 QuestionVO 列表
                List<QuestionVO> questionVOList = questions.stream().map(question -> {
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
                        BeanUtils.copyProperties(answer,answerVO);
                        return answerVO;
                    }).collect(Collectors.toList());
                    questionVO.setAnswers(answerVOList);

                    return questionVO;
                }).collect(Collectors.toList());

                questionBankVO.setQuestions(questionVOList);
            }

            return questionBankVO;
        }).collect(Collectors.toList());

        return Result.success(questionBankVOList);
    }

}
