package com.xuan.controller.admin;

import com.xuan.domain.dto.BankReviewDTO;
import com.xuan.domain.vo.BankReviewVO;
import com.xuan.result.Result;
import com.xuan.service.IReviewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "题库审核相关接口", description = "管理员端 - 题库审核相关接口")
@RequestMapping("/api/admin/review")
@RestController
public class BankReviewController {

    @Autowired
    private IReviewsService reviewService;

    @GetMapping("/pending")
    @Operation(summary = "获取审核中题库列表")
    public Result<List<BankReviewVO>> getPendingBanks(
            @Parameter(description = "管理员ID", required = true) @RequestParam Integer adminId) {
        // 简单的管理员身份校验，实际应用中应使用更安全的认证机制，比如JWT
        if (adminId == null) {
            return Result.error("管理员ID不能为空");
        }
        // 可以在这里添加其他的逻辑判断
        return reviewService.getPendingBanks(adminId);
    }

    @PostMapping("/review")
    @Operation(summary = "审核题库")
    public Result<String> reviewBank(
            @Parameter(description = "管理员ID", required = true) @RequestParam Integer adminId,
            @RequestBody BankReviewDTO bankReviewDTO) {
        // 简单的管理员身份校验，实际应用中应使用更安全的认证机制
        if (adminId == null) {
            return Result.error("管理员ID不能为空");
        }
        // 可以在这里添加其他的逻辑判断
        return reviewService.reviewBank(adminId, bankReviewDTO);
    }

}