package com.xuan.service;

import com.xuan.domain.dto.BankReviewDTO;
import com.xuan.domain.entity.Reviews;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.domain.vo.BankReviewVO;
import com.xuan.result.Result;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
public interface IReviewsService extends IService<Reviews> {

    /**
     * 获取待审核题库列表
     * @param adminId
     * @return
     */
    Result<List<BankReviewVO>> getPendingBanks(Integer adminId);

    Result<String> reviewBank(Integer adminId, BankReviewDTO bankReviewDTO);
}
