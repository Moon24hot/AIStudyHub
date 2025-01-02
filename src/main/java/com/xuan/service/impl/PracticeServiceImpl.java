package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.xuan.domain.dto.QuestionAnswerDTO;
import com.xuan.domain.dto.UserAnswerDTO;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.QuestionBankDetailVO;
import com.xuan.domain.vo.QuestionWithOrderVO;
import com.xuan.mapper.*;
import com.xuan.result.Result;
import com.xuan.service.IPracticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PracticeServiceImpl extends ServiceImpl<QuestionBanksMapper, QuestionBanks> implements IPracticeService {

    @Autowired
    private QuestionBanksMapper questionBanksMapper;

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UserAnswersMapper userAnswersMapper;

    @Autowired
    private WrongQuestionsMapper wrongQuestionsMapper;

    @Autowired
    private FavoritesMapper favoritesMapper;

    /**
     * 根据题库ID查询题库详情
     *
     * @param bankId 题库ID
     * @return 题库详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<QuestionBankDetailVO> getQuestionBankDetail(Integer bankId) {
        // 1. 检查参数
        if (bankId == null) {
            return Result.error("题库ID不能为空");
        }

        // 2. 查询题库信息
        QuestionBanks questionBank = questionBanksMapper.selectById(bankId);
        if (questionBank == null) {
            return Result.error("题库不存在");
        }

        // 3. 查询题库中的题目ID列表
        List<QuestionBankItems> items = Db.lambdaQuery(QuestionBankItems.class)
                .eq(QuestionBankItems::getBankId, bankId)
                .list();
        if(items.isEmpty()){
            return Result.error("该题库没有任何题目");
        }
        List<Integer> questionIds = items.stream()
                .map(QuestionBankItems::getQuestionId)
                .collect(Collectors.toList());

        // 4. 查询题目详情
        List<Questions> questions = questionsMapper.selectBatchIds(questionIds);

        // 5. 查询题目的答案和标签 (优化：批量查询)
        List<Answers> allAnswers = Db.lambdaQuery(Answers.class).in(Answers::getQuestionId, questionIds).list();
        List<QuestionTags> allTags = Db.lambdaQuery(QuestionTags.class).in(QuestionTags::getQuestionId, questionIds).list();

        // 将答案和标签按照 questionId 分组
        Map<Integer, List<Answers>> answersMap = allAnswers.stream().collect(Collectors.groupingBy(Answers::getQuestionId));
        Map<Integer, List<QuestionTags>> tagsMap = allTags.stream().collect(Collectors.groupingBy(QuestionTags::getQuestionId));

        // 6. 按题目类型分类，并添加序号
        Map<String, List<QuestionWithOrderVO>> questionMap = new HashMap<>();
        AtomicInteger singleChoiceOrder = new AtomicInteger(1);
        AtomicInteger multipleChoiceOrder = new AtomicInteger(1);
        AtomicInteger subjectiveOrder = new AtomicInteger(1);

        questions.forEach(question -> {
            QuestionWithOrderVO vo = new QuestionWithOrderVO();
            BeanUtils.copyProperties(question, vo);
            vo.setQuestionId(question.getId());
            vo.setAnswers(answersMap.getOrDefault(question.getId(), new ArrayList<>()));
            vo.setTags(tagsMap.getOrDefault(question.getId(), new ArrayList<>()));

            String questionType = question.getType();
            if (!questionMap.containsKey(questionType)) {
                questionMap.put(questionType, new ArrayList<>());
            }

            if ("单选题".equals(questionType)) {
                vo.setOrder(singleChoiceOrder.getAndIncrement());
            } else if ("多选题".equals(questionType)) {
                vo.setOrder(multipleChoiceOrder.getAndIncrement());
            } else if ("主观题".equals(questionType)) {
                vo.setOrder(subjectiveOrder.getAndIncrement());
            }

            questionMap.get(questionType).add(vo);
        });

        // 7. 组装 QuestionBankDetailVO
        QuestionBankDetailVO detailVO = new QuestionBankDetailVO();
        detailVO.setBankId(questionBank.getId());
        detailVO.setTitle(questionBank.getTitle());
        detailVO.setDescription(questionBank.getDescription());
        detailVO.setSingleChoiceQuestions(questionMap.getOrDefault("单选题", new ArrayList<>()));
        detailVO.setMultipleChoiceQuestions(questionMap.getOrDefault("多选题", new ArrayList<>()));
        detailVO.setSubjectiveQuestions(questionMap.getOrDefault("主观题", new ArrayList<>()));

        return Result.success(detailVO);
    }

    /**
     * 保存做题进度
     *
     * @param userAnswerDTO 用户答案DTO
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveProgress(UserAnswerDTO userAnswerDTO) {
        //todo: 没有校验题库是否存在该题目，以及该题目的答案类型是否与题目类型相匹配

        // 1. 检查参数
        if (userAnswerDTO.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (userAnswerDTO.getBankId() == null) {
            return Result.error("题库ID不能为空");
        }
        if (CollectionUtils.isEmpty(userAnswerDTO.getQuestionAnswers())) {
            return Result.error("题目答案不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userAnswerDTO.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题库是否存在
        QuestionBanks questionBank = questionBanksMapper.selectById(userAnswerDTO.getBankId());
        if (questionBank == null) {
            return Result.error("题库不存在");
        }

        // 4. 遍历题目答案列表，逐个保存
        List<QuestionAnswerDTO> questionAnswers = userAnswerDTO.getQuestionAnswers();
        for (QuestionAnswerDTO questionAnswer : questionAnswers) {
            // 4.1 检查题目ID
            Integer questionId = questionAnswer.getQuestionId();
            if (questionId == null) {
                return Result.error("题目ID不能为空");
            }
            //检查题目是否存在
            Questions question = questionsMapper.selectById(questionAnswer.getQuestionId());
            if(question == null){
                return Result.error("题目不存在");
            }

            // 4.2 保存答题记录
            UserAnswers userAnswer = new UserAnswers();
            userAnswer.setUserId(userAnswerDTO.getUserId());
            userAnswer.setBankId(userAnswerDTO.getBankId());
            userAnswer.setQuestionId(questionId);
            userAnswer.setSelectedOptions(questionAnswer.getSelectedOptions());
            userAnswer.setSubjectiveAnswer(questionAnswer.getSubjectiveAnswer());
            //判断该题目用户之前是否已经做过，做过的话更新答案记录，没做过则插入一条新的记录
            UserAnswers one = userAnswersMapper.selectOne(new LambdaQueryWrapper<UserAnswers>()
                    .eq(UserAnswers::getUserId, userAnswerDTO.getUserId())
                    .eq(UserAnswers::getBankId, userAnswerDTO.getBankId())
                    .eq(UserAnswers::getQuestionId, questionAnswer.getQuestionId()));
            if(one != null){
                userAnswer.setId(one.getId());
                userAnswersMapper.updateById(userAnswer);
            }else {
                userAnswersMapper.insert(userAnswer);
            }
        }

        return Result.success("做题进度保存成功");
    }

    /**
     * 根据用户ID和题库ID清空做题进度
     * @param userId
     * @param bankId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> clearProgress(Integer userId, Integer bankId) {
        // 1. 检查参数
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        if (bankId == null) {
            return Result.error("题库ID不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题库是否存在
        QuestionBanks questionBank = questionBanksMapper.selectById(bankId);
        if (questionBank == null) {
            return Result.error("题库不存在");
        }

        // 4. 删除答题记录
        LambdaQueryWrapper<UserAnswers> queryWrapper = new LambdaQueryWrapper<UserAnswers>()
                .eq(UserAnswers::getUserId, userId)
                .eq(UserAnswers::getBankId, bankId);
        userAnswersMapper.delete(queryWrapper);

        return Result.success("做题进度已清空");
    }

    /**
     * 记录错题
     * @param userId
     * @param questionId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addWrongQuestion(Integer userId, Integer questionId) {
        // 1. 检查参数
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        if (questionId == null) {
            return Result.error("题目ID不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题目是否存在
        Questions question = questionsMapper.selectById(questionId);
        if (question == null) {
            return Result.error("题目不存在");
        }

        // 4. 检查错题记录是否已存在
        WrongQuestions existingWrongQuestion = wrongQuestionsMapper.selectOne(
                new LambdaQueryWrapper<WrongQuestions>()
                        .eq(WrongQuestions::getUserId, userId)
                        .eq(WrongQuestions::getQuestionId, questionId)
        );
        if (existingWrongQuestion != null) {
            return Result.error("该错题已存在");
        }

        // 5. 添加错题记录
        WrongQuestions wrongQuestion = new WrongQuestions();
        wrongQuestion.setUserId(userId);
        wrongQuestion.setQuestionId(questionId);
        wrongQuestionsMapper.insert(wrongQuestion);

        return Result.success("错题记录添加成功");
    }

    /**
     * 收藏题目
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> favoriteQuestion(Integer userId, Integer questionId) {
        // 1. 检查参数
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        if (questionId == null) {
            return Result.error("题目ID不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题目是否存在
        Questions question = questionsMapper.selectById(questionId);
        if (question == null) {
            return Result.error("题目不存在");
        }

        // 4. 检查是否已收藏
        Favorites existingFavorite = favoritesMapper.selectOne(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getItemId, questionId)
                        .eq(Favorites::getType, "题目")
        );
        if (existingFavorite != null) {
            return Result.error("该题目已收藏");
        }

        // 5. 添加收藏记录
        Favorites favorite = new Favorites();
        favorite.setUserId(userId);
        favorite.setItemId(questionId);
        favorite.setType("题目");
        favoritesMapper.insert(favorite);

        return Result.success("题目收藏成功");
    }

}