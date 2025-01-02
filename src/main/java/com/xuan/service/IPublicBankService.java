package com.xuan.service;

import com.xuan.domain.vo.PublicBankVO;
import com.xuan.result.Result;

import java.util.List;

public interface IPublicBankService {
    /**
     * 查看公开题库列表
     *
     * @return 公开题库列表
     */
    Result<List<PublicBankVO>> listPublicBanks();

    /**
     * 收藏题库
     *
     * @param userId 用户ID
     * @param bankId 题库ID
     * @return 结果
     */
    Result<String> collectBank(Integer userId, Integer bankId);
}