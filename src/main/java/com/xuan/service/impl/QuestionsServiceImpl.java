package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.xuan.domain.dto.AnswerAddDTO;
import com.xuan.domain.dto.AnswerUpdateDTO;
import com.xuan.domain.dto.QuestionAddDTO;
import com.xuan.domain.dto.QuestionUpdateDTO;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.AnswerVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.mapper.*;
import com.xuan.result.Result;
import com.xuan.service.IQuestionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private FavoritesMapper favoritesMapper;

    @Autowired
    private QuestionBankItemsMapper questionBankItemsMapper;

    @Autowired
    private AnswersMapper answersMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private QuestionTagsMapper questionTagsMapper;

    @Autowired
    private UserAnswersMapper userAnswersMapper;

    @Autowired
    private WrongQuestionsMapper wrongQuestionsMapper;

    /**
     * 新增题目
     *
     * @param questionAddDTO
     * @return
     */
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

    /**
     * 获取题目列表
     *
     * @param userId 用户ID
     * @return
     */
    @Transactional
    @Override
    public Result<List<QuestionVO>> getQuestionsByUserId(Integer userId) {
        // 1. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 查询并收集所有相关的题目ID
        Set<Integer> allQuestionIds = new HashSet<>();

        // 2.1 查询用户创建的题目ID
        List<Questions> createdQuestions = questionsMapper.selectList(
                new LambdaQueryWrapper<Questions>().eq(Questions::getCreatorId, userId));
        allQuestionIds.addAll(createdQuestions.stream().map(Questions::getId).collect(Collectors.toSet()));

        // 2.2 查询用户收藏的题库中的题目ID
        List<Favorites> favorites = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题库"));

        if (!CollectionUtils.isEmpty(favorites)) {
            List<Integer> bankIds = favorites.stream().map(Favorites::getItemId).collect(Collectors.toList());

            List<QuestionBankItems> bankItems = questionBankItemsMapper.selectList(
                    new LambdaQueryWrapper<QuestionBankItems>().in(QuestionBankItems::getBankId, bankIds));

            allQuestionIds.addAll(bankItems.stream().map(QuestionBankItems::getQuestionId).collect(Collectors.toSet()));
        }

        // 3. 如果没有题目，直接返回空列表
        if (allQuestionIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 4. 根据题目ID列表查询题目详情 (这里使用 MyBatis-Plus 的 listByIds 方法)
        List<Questions> questions = questionsMapper.selectBatchIds(allQuestionIds);

        // 5. 构造 QuestionVO 列表
        List<QuestionVO> questionVOList = questions.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            questionVO.setQuestionId(question.getId());
            questionVO.setContent(question.getContent());
            questionVO.setType(question.getType());

            // 设置创建者用户名
            Users creator = usersMapper.selectById(question.getCreatorId());
            questionVO.setCreatorName(creator.getUsername());

            // 设置答案信息
            List<Answers> answers = answersMapper.selectList(
                    new LambdaQueryWrapper<Answers>().eq(Answers::getQuestionId, question.getId()));
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


    /**
     * 编辑题目
     * @param questionUpdateDTO 题目更新DTO
     * @return
     */
    @Override
    @Transactional
    public Result<String> updateQuestion(QuestionUpdateDTO questionUpdateDTO) {
        // 1. 检查参数
        if (questionUpdateDTO.getQuestionId() == null) {
            return Result.error("题目ID不能为空");
        }
        if (questionUpdateDTO.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查题目是否存在以及是否属于该用户
        Questions question = questionsMapper.selectById(questionUpdateDTO.getQuestionId());
        if (question == null) {
            return Result.error("题目不存在");
        }
        if (!question.getCreatorId().equals(questionUpdateDTO.getUserId())) {
            return Result.error("无权修改该题目");
        }

        // 3. 更新题目信息 (只更新题目内容)
        LambdaUpdateWrapper<Questions> questionUpdateWrapper = new LambdaUpdateWrapper<Questions>()
                .eq(Questions::getId, questionUpdateDTO.getQuestionId())
                .set(Questions::getContent, questionUpdateDTO.getContent());
        questionsMapper.update(null, questionUpdateWrapper);

        // 4. 更新答案信息
        if ("主观题".equals(question.getType())) {
            // 主观题更新答案
            if (questionUpdateDTO.getSubjectiveAnswer() != null) {
                // 先查询该题目是否已经存在答案 (通常主观题应该只有一个答案)
                Answers existingAnswer = answersMapper.selectOne(
                        new LambdaQueryWrapper<Answers>()
                                .eq(Answers::getQuestionId, questionUpdateDTO.getQuestionId()));

                if (existingAnswer != null) {
                    // 如果已存在答案，则更新答案
                    LambdaUpdateWrapper<Answers> answerUpdateWrapper = new LambdaUpdateWrapper<Answers>()
                            .eq(Answers::getId, existingAnswer.getId())
                            .set(Answers::getSubjectiveAnswer, questionUpdateDTO.getSubjectiveAnswer());
                    answersMapper.update(null, answerUpdateWrapper);
                } else {
                    // 如果不存在答案，则插入新答案
                    Answers newAnswer = new Answers();
                    newAnswer.setQuestionId(questionUpdateDTO.getQuestionId());
                    newAnswer.setSubjectiveAnswer(questionUpdateDTO.getSubjectiveAnswer());
                    answersMapper.insert(newAnswer);
                }
            }
        } else {
            // 单选题/多选题更新答案
            if (!CollectionUtils.isEmpty(questionUpdateDTO.getAnswers())) {
                // 检查是否有正确答案
                boolean hasCorrectAnswer = questionUpdateDTO.getAnswers().stream()
                        .anyMatch(AnswerUpdateDTO::getIsCorrect);
                if (!hasCorrectAnswer) {
                    return Result.error("至少需要一个正确答案");
                }

                for (AnswerUpdateDTO answerUpdateVO : questionUpdateDTO.getAnswers()) {
                    if (answerUpdateVO.getAnswerId() == null) {
                        return Result.error("答案ID不能为空");
                    }
                    LambdaUpdateWrapper<Answers> answerUpdateWrapper = new LambdaUpdateWrapper<Answers>()
                            .eq(Answers::getId, answerUpdateVO.getAnswerId())
                            .set(Answers::getOptionContent, answerUpdateVO.getOptionContent())
                            .set(Answers::getIsCorrect, answerUpdateVO.getIsCorrect());
                    answersMapper.update(null, answerUpdateWrapper);
                }
            }
        }

        return Result.success("题目修改成功");
    }

    /**
     * 删除题目
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return
     */
    @Override
    @Transactional
    public Result<String> deleteQuestion(Integer userId, Integer questionId) {
        // 1. 检查参数
        if (questionId == null) {
            return Result.error("题目ID不能为空");
        }
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查题目是否存在以及是否属于该用户
        Questions question = getById(questionId);
        if (question == null) {
            return Result.error("题目不存在");
        }
        if (!question.getCreatorId().equals(userId)) {
            return Result.error("无权删除该题目");
        }

        // 3. 删除题目本身
        questionsMapper.deleteById(questionId);

        // 4. 删除相关的其他表记录
        // 4.1 删除题目答案表 (answers)
        answersMapper.delete(new LambdaQueryWrapper<Answers>().eq(Answers::getQuestionId, questionId));

        // 4.2 删除题目标签表 (question_tags)
        questionTagsMapper.delete(new LambdaQueryWrapper<QuestionTags>().eq(QuestionTags::getQuestionId, questionId));

        // 4.3 删除题库详细表 (question_bank_items)
        questionBankItemsMapper.delete(new LambdaQueryWrapper<QuestionBankItems>().eq(QuestionBankItems::getQuestionId, questionId));

        // 4.4 删除答题记录表 (user_answers)
        userAnswersMapper.delete(new LambdaQueryWrapper<UserAnswers>().eq(UserAnswers::getQuestionId, questionId));

        // 4.5 删除错题表 (wrong_questions)
        wrongQuestionsMapper.delete(new LambdaQueryWrapper<WrongQuestions>().eq(WrongQuestions::getQuestionId, questionId));

        // 4.6 删除收藏表 (favorites)
        favoritesMapper.delete(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getItemId, questionId)
                .eq(Favorites::getType, "题目"));

        return Result.success("题目删除成功");
    }
}

