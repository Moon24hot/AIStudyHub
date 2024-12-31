package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.xuan.domain.dto.AnswerAddDTO;
import com.xuan.domain.dto.QuestionAddDTO;
import com.xuan.domain.entity.Answers;
import com.xuan.domain.entity.QuestionTags;
import com.xuan.domain.entity.Questions;
import com.xuan.mapper.QuestionsMapper;
import com.xuan.result.Result;
import com.xuan.service.IQuestionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Service
public class QuestionsServiceImpl extends ServiceImpl<QuestionsMapper, Questions> implements IQuestionsService {

    @Transactional
    public Result<String> addQuestion(QuestionAddDTO questionAddDTO) {
        // 1. 健壮性检查
        if (!"单选题".equals(questionAddDTO.getType()) && !"多选题".equals(questionAddDTO.getType()) && !"主观题".equals(questionAddDTO.getType())) {
            return Result.error("题目类型错误");
        }
        if (StringUtils.isEmpty(questionAddDTO.getContent())) {
            return Result.error("题目内容不能为空");
        }
        if (CollectionUtils.isEmpty(questionAddDTO.getAnswers())) {
            return Result.error("答案不能为空");
        }
        if (questionAddDTO.getAnswers().stream().allMatch(answer -> answer.getIsCorrect() == null || !answer.getIsCorrect())) {
            if (!"主观题".equals(questionAddDTO.getType()))
                return Result.error("必须选择正确答案");
        }

        // 2. 插入题目表
        Questions question = new Questions();
        question.setType(questionAddDTO.getType());
        question.setContent(questionAddDTO.getContent());
        question.setCreatorId(questionAddDTO.getUserId());
        question.setCreateTime(LocalDateTime.now());
        this.save(question); // 使用 MyBatis-Plus 的 save 方法

        // 3. 插入答案表
        List<Answers> answers = new ArrayList<>();
        for (AnswerAddDTO answerAddDTO : questionAddDTO.getAnswers()) {
            Answers answer = new Answers();
            answer.setQuestionId(question.getId());
            if (!"主观题".equals(questionAddDTO.getType())) {
                answer.setOptionLabel(answerAddDTO.getOptionLabel());
                answer.setOptionContent(answerAddDTO.getOptionContent());
                answer.setIsCorrect(answerAddDTO.getIsCorrect());
            } else {
                answer.setSubjectiveAnswer(answerAddDTO.getSubjectiveAnswer());
            }
            answers.add(answer);
        }
        Db.saveBatch(answers);//使用mybatisplus批量插入

        // 4. 插入标签表
        if (!CollectionUtils.isEmpty(questionAddDTO.getTags())) {
            List<QuestionTags> questionTags = new ArrayList<>();
            for (String tag : questionAddDTO.getTags()) {
                QuestionTags questionTag = new QuestionTags();
                questionTag.setQuestionId(question.getId());
                questionTag.setTagName(tag);
                questionTags.add(questionTag);
            }
            Db.saveBatch(questionTags);//使用mybatisplus批量插入
        }

        return Result.success("新增题目成功");
    }
}
