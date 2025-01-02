package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuan.domain.dto.GenerateBankDTO;
import com.xuan.domain.dto.QuestionBankCreateDTO;
import com.xuan.domain.dto.QuestionBankUpdateDTO;
import com.xuan.domain.dto.GenerateBankDtoRequirement;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.CollectedBankVO;
import com.xuan.domain.vo.GeneratedQuestionBankVO;
import com.xuan.domain.vo.QuestionBankVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.enums.BanksStatus;
import com.xuan.mapper.*;
import com.xuan.result.Result;
import com.xuan.service.IQuestionBanksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
public class QuestionBanksServiceImpl extends ServiceImpl<QuestionBanksMapper, QuestionBanks> implements IQuestionBanksService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private QuestionBanksMapper questionBanksMapper;

    @Autowired
    private QuestionsMapper questionsMapper; // 注入 QuestionsMapper

    @Autowired
    private UserAnswersMapper userAnswersMapper;

    @Autowired
    private ReviewsMapper reviewsMapper;

    @Autowired
    private FavoritesMapper favoritesMapper;

    @Autowired
    private QuestionTagsMapper questionTagsMapper;

    @Autowired
    private QuestionBankItemsMapper questionBankItemsMapper;

    @Autowired
    private ObjectMapper objectMapper; // 用于 JSON 处理

    @Value("classpath:prompts/generateBankPrompt.st")
    private Resource generateBankPrompt;

    @Autowired
    private OpenAiChatModel chatModel;


    /**
     * 创建题库
     *
     * @param questionBankCreateDTO 题库创建DTO
     * @return
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    @Transactional
    public Result<String> createQuestionBank(QuestionBankCreateDTO questionBankCreateDTO) {
        //todo 最好校验一下题目是不是都是该用户创建的或者是收藏题库带来的

        // 1. 检查参数
        if (questionBankCreateDTO.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (questionBankCreateDTO.getTitle() == null || questionBankCreateDTO.getTitle().trim().isEmpty()) {
            return Result.error("题库标题不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(questionBankCreateDTO.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题目ID是否存在
        if (!CollectionUtils.isEmpty(questionBankCreateDTO.getQuestionIds())) {
            Set<Integer> questionIds = new HashSet<>(questionBankCreateDTO.getQuestionIds());
            List<Questions> existingQuestions = questionsMapper.selectBatchIds(questionIds);
            if (existingQuestions.size() != questionIds.size()) {
                Set<Integer> existingQuestionIds = new HashSet<>();
                for (Questions q : existingQuestions) {
                    existingQuestionIds.add(q.getId());
                }
                questionIds.removeAll(existingQuestionIds);
                return Result.error("部分题目ID不存在: " + questionIds);
            }
        }

        // 4. 创建题库记录
        QuestionBanks questionBank = new QuestionBanks();
        questionBank.setCreatorId(questionBankCreateDTO.getUserId());
        questionBank.setTitle(questionBankCreateDTO.getTitle());
        questionBank.setDescription(questionBankCreateDTO.getDescription());
        questionBank.setCreateTime(LocalDateTime.now());
        questionBanksMapper.insert(questionBank);

        // 5. 插入题库详细表记录
        if (!CollectionUtils.isEmpty(questionBankCreateDTO.getQuestionIds())) {
            List<QuestionBankItems> items = new ArrayList<>();
            for (Integer questionId : questionBankCreateDTO.getQuestionIds()) {
                QuestionBankItems item = new QuestionBankItems();
                item.setBankId(questionBank.getId());
                item.setQuestionId(questionId);
                items.add(item);
            }
            Db.saveBatch(items);
        }

        return Result.success("题库创建成功");
    }

    /**
     * 根据用户id查询题库
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    @Transactional
    public Result<List<QuestionBankVO>> getQuestionBanksByUserId(Integer userId) {
        // 1. 检查参数
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 查询题库列表
        List<QuestionBanks> questionBanksList = this.list(new LambdaQueryWrapper<QuestionBanks>()
                .eq(QuestionBanks::getCreatorId, userId));

        // 4. 转换为 VO 列表
        List<QuestionBankVO> questionBankVOList = questionBanksList.stream()
                .map(questionBank -> {
                    QuestionBankVO questionBankVO = new QuestionBankVO();
                    BeanUtils.copyProperties(questionBank, questionBankVO);
                    questionBankVO.setBankId(questionBank.getId());
                    return questionBankVO;
                })
                .collect(Collectors.toList());

        return Result.success(questionBankVOList);
    }

    /**
     * 编辑题库
     *
     * @param questionBankUpdateDTO 题库更新DTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateQuestionBank(QuestionBankUpdateDTO questionBankUpdateDTO) {
        //todo 最好校验一下题目是不是都是该用户创建的或者是收藏题库带来的

        // 1. 检查参数
        if (questionBankUpdateDTO.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (questionBankUpdateDTO.getBankId() == null) {
            return Result.error("题库ID不能为空");
        }
        if (questionBankUpdateDTO.getTitle() == null || questionBankUpdateDTO.getTitle().trim().isEmpty()) {
            return Result.error("题库标题不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(questionBankUpdateDTO.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题库是否存在以及是否属于该用户
        QuestionBanks questionBank = this.getById(questionBankUpdateDTO.getBankId());
        if (questionBank == null) {
            return Result.error("题库不存在");
        }
        if (!questionBank.getCreatorId().equals(questionBankUpdateDTO.getUserId())) {
            return Result.error("无权修改该题库");
        }

        // 4. 检查题库状态是否可以修改
        if (questionBank.getStatus() != BanksStatus.NOT_SHARED && questionBank.getStatus() != BanksStatus.REJECTED) {
            return Result.error("题库状态不允许修改");
        }

        // 5. 检查题目ID是否存在
        if (!CollectionUtils.isEmpty(questionBankUpdateDTO.getQuestionIds())) {
            Set<Integer> questionIds = new HashSet<>(questionBankUpdateDTO.getQuestionIds());
            List<Questions> existingQuestions = questionsMapper.selectBatchIds(questionIds);
            if (existingQuestions.size() != questionIds.size()) {
                Set<Integer> existingQuestionIds = existingQuestions.stream()
                        .map(Questions::getId)
                        .collect(Collectors.toSet());
                questionIds.removeAll(existingQuestionIds);
                return Result.error("部分题目ID不存在: " + questionIds);
            }
        }

        // 6. 更新题库信息
        LambdaUpdateWrapper<QuestionBanks> updateWrapper = new LambdaUpdateWrapper<QuestionBanks>()
                .eq(QuestionBanks::getId, questionBankUpdateDTO.getBankId())
                .set(QuestionBanks::getTitle, questionBankUpdateDTO.getTitle())
                .set(QuestionBanks::getDescription, questionBankUpdateDTO.getDescription());

        // 7. 如果原先状态是"被拒绝"，则改为"未分享"
        if (questionBank.getStatus() == BanksStatus.REJECTED) {
            updateWrapper.set(QuestionBanks::getStatus, BanksStatus.NOT_SHARED);
        }
        this.update(updateWrapper);

        // 8. 更新题库详细表
        // 8.1 查询原题库中的题目ID
        List<QuestionBankItems> originalItems = Db.lambdaQuery(QuestionBankItems.class)
                .eq(QuestionBankItems::getBankId, questionBankUpdateDTO.getBankId())
                .list();
        Set<Integer> originalQuestionIds = originalItems.stream()
                .map(QuestionBankItems::getQuestionId)
                .collect(Collectors.toSet());

        // 8.2 获取需要添加和需要删除的题目ID
        Set<Integer> newQuestionIds = new HashSet<>(questionBankUpdateDTO.getQuestionIds());
        Set<Integer> toAddQuestionIds = new HashSet<>(newQuestionIds);
        toAddQuestionIds.removeAll(originalQuestionIds); // 需要添加的题目ID

        Set<Integer> toRemoveQuestionIds = new HashSet<>(originalQuestionIds);
        toRemoveQuestionIds.removeAll(newQuestionIds); // 需要删除的题目ID

        // 8.3 删除题目
        if (!toRemoveQuestionIds.isEmpty()) {
            Db.lambdaUpdate(QuestionBankItems.class)
                    .eq(QuestionBankItems::getBankId, questionBankUpdateDTO.getBankId())
                    .in(QuestionBankItems::getQuestionId, toRemoveQuestionIds)
                    .remove();
        }

        // 8.4 添加题目
        if (!toAddQuestionIds.isEmpty()) {
            List<QuestionBankItems> newItems = new ArrayList<>();
            for (Integer questionId : toAddQuestionIds) {
                QuestionBankItems item = new QuestionBankItems();
                item.setBankId(questionBankUpdateDTO.getBankId());
                item.setQuestionId(questionId);
                newItems.add(item);
            }
            Db.saveBatch(newItems);
        }

        // 9. 删除用户答题记录表对应数据
        if (!toRemoveQuestionIds.isEmpty()) {
            LambdaQueryWrapper<UserAnswers> queryWrapper = new LambdaQueryWrapper<UserAnswers>()
                    .eq(UserAnswers::getBankId, questionBankUpdateDTO.getBankId())
                    .in(UserAnswers::getQuestionId, toRemoveQuestionIds);
            userAnswersMapper.delete(queryWrapper);
        }

        return Result.success("题库修改成功");
    }


    /**
     * 删除题库
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteQuestionBank(Integer userId, Integer bankId) {
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

        // 3. 检查题库是否存在以及是否属于该用户
        QuestionBanks questionBank = this.getById(bankId);
        if (questionBank == null) {
            return Result.error("题库不存在");
        }
        if (!questionBank.getCreatorId().equals(userId)) {
            return Result.error("无权删除该题库");
        }

        // 4. 检查题库状态是否可以删除
        if (questionBank.getStatus() != BanksStatus.NOT_SHARED && questionBank.getStatus() != BanksStatus.REJECTED) {
            return Result.error("题库状态不允许删除");
        }

        // 5. 删除题库
        this.removeById(bankId);

        // 6. 删除题库详细表相关记录
        Db.lambdaUpdate(QuestionBankItems.class)
                .eq(QuestionBankItems::getBankId, bankId)
                .remove();

        // 7. 删除答题记录表相关记录
        userAnswersMapper.delete(new LambdaQueryWrapper<UserAnswers>()
                .eq(UserAnswers::getBankId, bankId));

        return Result.success("题库删除成功");
    }

    /**
     * 申请题库公开
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> applyPublic(Integer userId, Integer bankId) {
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

        // 3. 检查题库是否存在以及是否属于该用户
        QuestionBanks questionBank = this.getById(bankId);
        if (questionBank == null) {
            return Result.error("题库不存在");
        }
        if (!questionBank.getCreatorId().equals(userId)) {
            return Result.error("无权操作该题库");
        }

        // 4. 检查题库状态是否可以申请公开
        if (questionBank.getStatus() != BanksStatus.NOT_SHARED) {
            return Result.error("题库状态不允许申请公开");
        }

        // 5. 更新题库状态为“审核中”
        LambdaUpdateWrapper<QuestionBanks> updateWrapper = new LambdaUpdateWrapper<QuestionBanks>()
                .eq(QuestionBanks::getId, bankId)
                .set(QuestionBanks::getStatus, BanksStatus.PENDING);
        this.update(updateWrapper);

        // 6. 插入审核表记录
        Reviews review = new Reviews();
        review.setItemId(bankId);
        reviewsMapper.insert(review);

        return Result.success("题库申请公开成功");
    }

    /**
     * 根据题库ID查询题库所有题目ID
     *
     * @param bankId 题库ID
     * @param userId 用户ID
     * @return 题目ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<Integer>> getQuestionIdsByBankId(Integer bankId, Integer userId) {
        // 1. 检查参数
        if (bankId == null) {
            return Result.error("题库ID不能为空");
        }
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 检查题库是否存在以及是否属于该用户
        QuestionBanks questionBank = this.getById(bankId);
        if (questionBank == null) {
            return Result.error("题库不存在");
        }
        if (!questionBank.getCreatorId().equals(userId)) {
            return Result.error("无权访问该题库");
        }

        // 4. 查询题库中的题目ID
        List<QuestionBankItems> items = Db.lambdaQuery(QuestionBankItems.class)
                .eq(QuestionBankItems::getBankId, bankId)
                .list();

        List<Integer> questionIds = items.stream()
                .map(QuestionBankItems::getQuestionId)
                .collect(Collectors.toList());

        return Result.success(questionIds);
    }

    /**
     * 查看收藏题库列表
     *
     * @param userId 用户ID
     * @return 收藏题库列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<CollectedBankVO>> listCollectedBanks(Integer userId) {
        // 1. 检查参数
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 查询该用户收藏的题库ID列表
        List<Favorites> favorites = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题库")
        );

        //如果该用户没有任何收藏，则返回空列表
        if (favorites.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Integer> bankIds = favorites.stream()
                .map(Favorites::getItemId)
                .collect(Collectors.toList());

        // 4. 查询题库信息
        List<QuestionBanks> collectedBanks = this.listByIds(bankIds);

        // 5. 查询题库创建者ID列表并去重
        List<Integer> creatorIds = collectedBanks.stream()
                .map(QuestionBanks::getCreatorId)
                .distinct()
                .collect(Collectors.toList());

        // 6. 批量查询创建者信息
        List<Users> creators = usersMapper.selectBatchIds(creatorIds);

        // 7. 将创建者信息转换为 Map，便于后续使用
        Map<Integer, String> creatorNameMap = creators.stream()
                .collect(Collectors.toMap(Users::getId, Users::getUsername));

        // 8. 组装 CollectedBankVO 列表
        List<CollectedBankVO> collectedBankVOList = collectedBanks.stream()
                .map(bank -> {
                    CollectedBankVO vo = new CollectedBankVO();
                    vo.setBankId(bank.getId());
                    vo.setTitle(bank.getTitle());
                    vo.setDescription(bank.getDescription());
                    vo.setCreatorName(creatorNameMap.get(bank.getCreatorId()));
                    return vo;
                })
                .collect(Collectors.toList());

        return Result.success(collectedBankVOList);
    }

    /**
     * 取消收藏题库
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> uncollectBank(Integer userId, Integer bankId) {
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

        // 4. 检查是否已收藏
        Favorites existingFavorite = favoritesMapper.selectOne(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getItemId, bankId)
                        .eq(Favorites::getType, "题库")
        );
        if (existingFavorite == null) {
            return Result.error("该题库未被收藏");
        }

        // 5. 删除收藏记录
        favoritesMapper.deleteById(existingFavorite.getId());

        return Result.success("取消收藏成功");
    }

    @Override
    public Result<GeneratedQuestionBankVO> generateQuestionBank(GenerateBankDTO generateBankDTO) {
        Integer userId = generateBankDTO.getUserId();
        String requirement = generateBankDTO.getRequirement();

        // 1. 收集用户拥有的所有题目ID
        Set<Integer> allQuestionIds = new HashSet<>();

        // 1.1 查询用户创建的题目ID
        List<Questions> createdQuestions = questionsMapper.selectList(
                new LambdaQueryWrapper<Questions>().eq(Questions::getCreatorId, userId));
        allQuestionIds.addAll(createdQuestions.stream().map(Questions::getId).collect(Collectors.toSet()));

        // 1.2 查询用户收藏的题目ID
        List<Favorites> favoriteQuestions = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题目"));
        allQuestionIds.addAll(favoriteQuestions.stream().map(Favorites::getItemId).collect(Collectors.toSet()));

        // 1.3 查询用户收藏的题库中的题目ID
        List<Favorites> favoriteBanks = favoritesMapper.selectList(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getType, "题库"));
        List<Integer> bankIds = favoriteBanks.stream().map(Favorites::getItemId).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(bankIds)) {
            List<QuestionBankItems> bankItems = questionBankItemsMapper.selectList(
                    new LambdaQueryWrapper<QuestionBankItems>()
                            .in(QuestionBankItems::getBankId, bankIds));
            allQuestionIds.addAll(bankItems.stream().map(QuestionBankItems::getQuestionId).collect(Collectors.toSet()));
        }

        // 2. 如果用户没有任何题目，返回错误信息
        if (allQuestionIds.isEmpty()) {
            return Result.error("您没有任何题目，无法生成题库");
        }

        // 3. 获取这些题目拥有的题目标签以及题目标签对应题目数量
        List<QuestionTags> questionTags = questionTagsMapper.selectList(
                new LambdaQueryWrapper<QuestionTags>()
                        .in(QuestionTags::getQuestionId, allQuestionIds));

        Map<String, Integer> tagCountMap = new HashMap<>();
        for (QuestionTags questionTag : questionTags) {
            String tagName = questionTag.getTagName();
            tagCountMap.put(tagName, tagCountMap.getOrDefault(tagName, 0) + 1);
        }

        // 4. 将题目标签和对应题目数量转换为 List<Map<String, Object>> 格式
        List<Map<String, Object>> tagList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tagCountMap.entrySet()) {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("tag", entry.getKey());
            tagMap.put("count", entry.getValue());
            tagList.add(tagMap);
        }

        // 5. 构建 prompt
        String tagListJson;
        try {
            tagListJson = objectMapper.writeValueAsString(tagList);
        } catch (IOException e) {
            return Result.error("标签列表转换为 JSON 失败");
        }

        // 读取 prompt 模板
        String promptTemplate;
        try {
            promptTemplate = new String(generateBankPrompt.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.error("读取 prompt 模板失败");
        }

        // 替换模板中的变量
        String prompt = promptTemplate
            .replace("{user_requirement}", requirement)
            .replace("{tags}", tagListJson);


        // 6. 调用 AI 大模型并获取响应
        String aiResponse = chatModel.call(prompt);

        // 去除 aiResponse 字符串开头和结尾的 ```json 和 ```
        if (aiResponse.startsWith("```json")) {
            aiResponse = aiResponse.substring(7, aiResponse.length() - 3);
        }

        // 7. 解析 AI 大模型的响应
        GenerateBankDtoRequirement generatedRequirements;
        try {
            generatedRequirements = objectMapper.readValue(aiResponse, GenerateBankDtoRequirement.class);
        } catch (IOException e) {
            return Result.error("AI 大模型响应解析失败" + e);
        }

        // 8. 根据 AI 大模型的响应构建题库
        GeneratedQuestionBankVO generatedBank = new GeneratedQuestionBankVO();
        generatedBank.setTitle(generatedRequirements.getTitle());
        generatedBank.setDescription(generatedRequirements.getDescription());

        // 9. 如果无法生成题库，直接返回
        if ("无法生成题库".equals(generatedBank.getTitle())) {
            return Result.success(generatedBank);
        }

        // 10. 根据正面需求选择题目
        List<QuestionVO> selectedQuestions = new ArrayList<>();
        for (GenerateBankDtoRequirement.TagCount positiveRequirement : generatedRequirements.getPositive_requirements()) {
            String requiredTag = positiveRequirement.getTag();
            int requiredCount = Integer.parseInt(positiveRequirement.getCount());

            // 获取拥有该标签的题目ID列表
            List<QuestionTags> tagQuestions = questionTagsMapper.selectList(
                    new LambdaQueryWrapper<QuestionTags>()
                            .eq(QuestionTags::getTagName, requiredTag)
                            .in(QuestionTags::getQuestionId, allQuestionIds));
            List<Integer> tagQuestionIds = tagQuestions.stream()
                    .map(QuestionTags::getQuestionId)
                    .collect(Collectors.toList());

            // 从这些题目中随机选择所需数量的题目
            Collections.shuffle(tagQuestionIds);
            List<Integer> selectedQuestionIds = tagQuestionIds.stream()
                    .limit(requiredCount)
                    .collect(Collectors.toList());

            // 查询题目详情
            if (!selectedQuestionIds.isEmpty()){
                List<Questions> questions = questionsMapper.selectBatchIds(selectedQuestionIds);
                // 转换为 QuestionVO
                List<QuestionVO> questionVOs = questions.stream().map(question -> {
                    QuestionVO questionVO = new QuestionVO();
                    questionVO.setQuestionId(question.getId());
                    questionVO.setContent(question.getContent());
                    questionVO.setType(question.getType());
                    return questionVO;
                }).collect(Collectors.toList());

                selectedQuestions.addAll(questionVOs);
            }
        }

        generatedBank.setQuestions(selectedQuestions);

        return Result.success(generatedBank);
    }
}
