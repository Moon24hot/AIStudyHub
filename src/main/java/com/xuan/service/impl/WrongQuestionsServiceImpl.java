package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.domain.entity.Answers;
import com.xuan.domain.entity.Questions;
import com.xuan.domain.entity.WrongQuestions;
import com.xuan.domain.vo.AnswerVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.mapper.AnswersMapper;
import com.xuan.mapper.QuestionsMapper;
import com.xuan.mapper.WrongQuestionsMapper;
import com.xuan.result.Result;
import com.xuan.service.IWrongQuestionsService;
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
public class WrongQuestionsServiceImpl extends ServiceImpl<WrongQuestionsMapper, WrongQuestions> implements IWrongQuestionsService {

    @Autowired
    private WrongQuestionsMapper wrongQuestionsMapper;

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private AnswersMapper answersMapper;

    @Override
    public Result<List<QuestionVO>> getWrongQuestions(Integer userId) {
        // 1. 查询用户错题ID列表
        List<WrongQuestions> wrongQuestions = wrongQuestionsMapper.selectList(
                new LambdaQueryWrapper<WrongQuestions>()
                        .eq(WrongQuestions::getUserId, userId));

        if (CollectionUtils.isEmpty(wrongQuestions)) {
            return Result.success(new ArrayList<>()); // 没有错题，返回空列表
        }

        List<Integer> questionIds = wrongQuestions.stream()
                .map(WrongQuestions::getQuestionId)
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
                BeanUtils.copyProperties(answer,answerVO);
                return answerVO;
            }).collect(Collectors.toList());
            questionVO.setAnswers(answerVOList);

            return questionVO;
        }).collect(Collectors.toList());

        return Result.success(questionVOList);
    }

}
