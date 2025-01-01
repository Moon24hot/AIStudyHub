package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.xuan.domain.dto.QuestionBankCreateDTO;
import com.xuan.domain.dto.QuestionBankUpdateDTO;
import com.xuan.domain.entity.*;
import com.xuan.domain.vo.QuestionBankVO;
import com.xuan.enums.BanksStatus;
import com.xuan.mapper.*;
import com.xuan.result.Result;
import com.xuan.service.IQuestionBankItemsService;
import com.xuan.service.IQuestionBanksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
     * @param questionBankUpdateDTO 题库更新DTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateQuestionBank(QuestionBankUpdateDTO questionBankUpdateDTO) {
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
}
