package com.xuan.controller.user;

import com.xuan.domain.dto.GenerateBankDTO;
import com.xuan.domain.dto.QuestionBankCreateDTO;
import com.xuan.domain.dto.QuestionBankUpdateDTO;
import com.xuan.domain.vo.GeneratedQuestionBankVO;
import com.xuan.domain.vo.CollectedBankVO;
import com.xuan.domain.vo.QuestionBankDetailVO;
import com.xuan.domain.vo.QuestionBankVO;
import com.xuan.result.Result;
import com.xuan.service.IPracticeService;
import com.xuan.service.IQuestionBanksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "我的题库/收藏题库相关接口", description = "用户端 - 我的题库/收藏题库相关接口")
@RequestMapping("/api/user/bank")
@RestController
public class BankController {

    @Autowired
    private IQuestionBanksService questionBanksService;

    @Autowired
    private IPracticeService practiceService;

    /**
     * 查询所有题库
     * @return
     */
//    @GetMapping("/list")
//    public List<QuestionBanks> getAllBanks(){
//        return questionBanksService.list();
//    }

    /**
     * 创建题库
     * @param questionBankCreateDTO
     * @return
     */
    @PostMapping("/create")
    @Operation(summary = "创建题库")
    public Result<String> createQuestionBank(@RequestBody QuestionBankCreateDTO questionBankCreateDTO) {
        return questionBanksService.createQuestionBank(questionBankCreateDTO);
    }

    /**
     * 根据用户ID查询题库列表
     * @param userId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据用户ID查询题库列表")
    public Result<List<QuestionBankVO>> getQuestionBanksByUserId(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        return questionBanksService.getQuestionBanksByUserId(userId);
    }

    /**
     * 根据题库ID修改题库
     * @param questionBankUpdateDTO
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "根据题库ID修改题库")
    public Result<String> updateQuestionBank(@RequestBody QuestionBankUpdateDTO questionBankUpdateDTO) {
        return questionBanksService.updateQuestionBank(questionBankUpdateDTO);
    }

    /**
     * 根据题库ID删除题库
     * @param userId
     * @param bankId
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "根据题库ID删除题库")
    public Result<String> deleteQuestionBank(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId) {
        return questionBanksService.deleteQuestionBank(userId, bankId);
    }

    /**
     * 申请题库公开
     * @param userId
     * @param bankId
     * @return
     */
    @PutMapping("/applyPublic")
    @Operation(summary = "申请题库公开")
    public Result<String> applyPublic(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId) {
        return questionBanksService.applyPublic(userId, bankId);
    }

    /**
     * 根据题库ID查询题库所有题目ID
     * @param bankId
     * @param userId
     * @return
     */
    @GetMapping("/questionIds")
    @Operation(summary = "根据题库ID查询题库所有题目ID")
    public Result<List<Integer>> getQuestionIdsByBankId(
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId,
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        return questionBanksService.getQuestionIdsByBankId(bankId, userId);
    }

    /**
     * 查看收藏题库列表
     * @param userId
     * @return
     */
    @GetMapping("/collectedList")
    @Operation(summary = "查看收藏题库列表")
    public Result<List<CollectedBankVO>> listCollectedBanks(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        return questionBanksService.listCollectedBanks(userId);
    }

    /**
     * 根据题库ID查询题库详情(做题功能)
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
     * 取消收藏题库
     * @param userId
     * @param bankId
     * @return
     */
    @PutMapping("/uncollect")
    @Operation(summary = "取消收藏题库")
    public Result<String> uncollectBank(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId) {
        return questionBanksService.uncollectBank(userId, bankId);
    }

    /**
     * AI生成题库
     * @param generateBankDTO
     * @return
     */
    @PostMapping("/AIGenerate")
    @Operation(summary = "AI生成题库")
    public Result<GeneratedQuestionBankVO> generateQuestionBank(@RequestBody GenerateBankDTO generateBankDTO) {
        if (generateBankDTO.getUserId() == null)
            return Result.error("用户ID不能为空");
        if (generateBankDTO.getRequirement() == null)
            return Result.error("题库生成需求不能为空");
        return questionBanksService.generateQuestionBank(generateBankDTO);
    }
}