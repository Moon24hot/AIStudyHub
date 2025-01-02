package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.AnswerVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.mapper.*;
import com.xuan.result.Result;
import com.xuan.service.IFavoritesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private QuestionBankItemsMapper questionBankItemsMapper;

    @Override
    public Result<List<QuestionVO>> getAllFavoriteQuestions(Integer userId) {
        // 1. 查询用户收藏的所有题目ID（包括收藏的题库中的题目）
        Set<Integer> allQuestionIds = new HashSet<>();

        // 1.1 查询用户直接收藏的题目ID
        List<Favorites> directFavorites = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题目"));
        allQuestionIds.addAll(directFavorites.stream().map(Favorites::getItemId).collect(Collectors.toSet()));

        // 1.2 查询用户收藏的题库中的题目ID
        List<Favorites> bankFavorites = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题库"));
        List<Integer> bankIds = bankFavorites.stream().map(Favorites::getItemId).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(bankIds)) {
            List<QuestionBankItems> bankItems = questionBankItemsMapper.selectList(
                    new LambdaQueryWrapper<QuestionBankItems>()
                            .in(QuestionBankItems::getBankId, bankIds));
            allQuestionIds.addAll(bankItems.stream().map(QuestionBankItems::getQuestionId).collect(Collectors.toSet()));
        }

        // 2. 如果没有收藏的题目，返回空列表
        if (allQuestionIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 3. 查询题目详情
        List<Questions> questions = questionsMapper.selectBatchIds(allQuestionIds);

        // 4. 构造 QuestionVO 列表
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

}
