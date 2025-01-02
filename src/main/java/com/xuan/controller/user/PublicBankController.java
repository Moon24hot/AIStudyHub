package com.xuan.controller.user;

import com.xuan.domain.vo.PublicBankVO;
import com.xuan.domain.vo.QuestionBankDetailVO;
import com.xuan.result.Result;
import com.xuan.service.IPracticeService;
import com.xuan.service.IPublicBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "题库广场相关接口", description = "用户端 - 题库广场相关接口")
@RequestMapping("/api/user/public/bank")
@RestController
public class PublicBankController {
    @Autowired
    private IPublicBankService publicBankService;

    @Autowired
    private IPracticeService practiceService;

    /**
     * 查看公开题库列表
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查看公开题库列表")
    public Result<List<PublicBankVO>> listPublicBanks() {
        return publicBankService.listPublicBanks();
    }

    /**
     * 根据题库ID查询题库详情"
     * @param bankId
     * @return
     */
    @GetMapping("/bankDetail")
    @Operation(summary = "根据题库ID查询题库详情")
    public Result<QuestionBankDetailVO> getQuestionBankDetail(
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId) {
        return practiceService.getQuestionBankDetail(bankId);
    }

    /**
     * 收藏题库
     * @param userId
     * @param bankId
     * @return
     */
    @PostMapping("/collect")
    @Operation(summary = "收藏题库")
    public Result<String> collectBank(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId) {
        return publicBankService.collectBank(userId, bankId);
    }
}