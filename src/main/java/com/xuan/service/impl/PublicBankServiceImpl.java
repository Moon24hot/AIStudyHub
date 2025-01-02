package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.domain.entity.Favorites;
import com.xuan.domain.entity.QuestionBanks;
import com.xuan.domain.entity.Users;
import com.xuan.domain.vo.PublicBankVO;
import com.xuan.enums.BanksStatus;
import com.xuan.mapper.FavoritesMapper;
import com.xuan.mapper.QuestionBanksMapper;
import com.xuan.mapper.UsersMapper;
import com.xuan.result.Result;
import com.xuan.service.IPublicBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PublicBankServiceImpl implements IPublicBankService {

    @Autowired
    private QuestionBanksMapper questionBanksMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private FavoritesMapper favoritesMapper;

    /**
     * 查看公开题库列表
     *
     * @return 公开题库列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<List<PublicBankVO>> listPublicBanks() {
        // 1. 查询所有状态为“已公开”的题库
        List<QuestionBanks> publicBanks = questionBanksMapper.selectList(
                new LambdaQueryWrapper<QuestionBanks>()
                        .eq(QuestionBanks::getStatus, BanksStatus.PUBLIC)
        );

        //如果没有已公开的题库,则返回空列表
        if (publicBanks.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        // 2. 获取题库创建者ID列表
        List<Integer> creatorIds = publicBanks.stream()
                .map(QuestionBanks::getCreatorId)
                .distinct()
                .collect(Collectors.toList());

        // 3. 批量查询创建者信息
        List<Users> creators = usersMapper.selectBatchIds(creatorIds);

        // 4. 将创建者信息转换为 Map，便于后续使用
        Map<Integer, String> creatorNameMap = creators.stream()
                .collect(Collectors.toMap(Users::getId, Users::getUsername));

        // 5. 组装 PublicBankVO 列表
        List<PublicBankVO> publicBankVOList = publicBanks.stream()
                .map(bank -> {
                    PublicBankVO vo = new PublicBankVO();
                    vo.setBankId(bank.getId());
                    vo.setTitle(bank.getTitle());
                    vo.setDescription(bank.getDescription());
                    vo.setCreatorName(creatorNameMap.get(bank.getCreatorId()));
                    return vo;
                })
                .collect(Collectors.toList());

        return Result.success(publicBankVOList);
    }

    /**
     * 收藏题库
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> collectBank(Integer userId, Integer bankId) {
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

        // 4. 检查题库状态是否为“已公开”
        if (questionBank.getStatus() != BanksStatus.PUBLIC) {
            return Result.error("该题库未公开，无法收藏");
        }

        // 5. 检查是否已收藏
        Favorites existingFavorite = favoritesMapper.selectOne(
                new LambdaQueryWrapper<Favorites>()
                        .eq(Favorites::getUserId, userId)
                        .eq(Favorites::getItemId, bankId)
                        .eq(Favorites::getType, "题库")
        );
        if (existingFavorite != null) {
            return Result.error("该题库已收藏");
        }

        // 6. 添加收藏记录
        Favorites favorite = new Favorites();
        favorite.setUserId(userId);
        favorite.setItemId(bankId);
        favorite.setType("题库");
        favoritesMapper.insert(favorite);

        return Result.success("题库收藏成功");
    }
}