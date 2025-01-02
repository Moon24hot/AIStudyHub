package com.xuan.controller.user;

import com.xuan.domain.dto.UserAnswerDTO;
import com.xuan.domain.vo.QuestionBankDetailVO;
import com.xuan.result.Result;
import com.xuan.service.IPracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "做题相关接口", description = "用户端 - 做题相关接口")
@RequestMapping("/api/user/practice")
@RestController
public class PracticeController {

    @Autowired
    private IPracticeService practiceService;

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
     * 保存做题进度
     * @param userAnswerDTO
     * @return
     */
    @PostMapping("/saveProgress")
    @Operation(summary = "保存做题进度")
    public Result<String> saveProgress(@RequestBody UserAnswerDTO userAnswerDTO) {
        return practiceService.saveProgress(userAnswerDTO);
    }

    /**
     * 根据用户ID和题库ID清空做题进度
     * @param userId
     * @param bankId
     * @return
     */
    @DeleteMapping("/clearProgress")
    @Operation(summary = "根据用户ID和题库ID清空做题进度")
    public Result<String> clearProgress(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题库ID", required = true) @RequestParam Integer bankId) {
        return practiceService.clearProgress(userId, bankId);
    }

    /**
     * 记录错题
     * @param userId
     * @param questionId
     * @return
     */
    @PostMapping("/addWrongQuestion")
    @Operation(summary = "记录错题")
    public Result<String> addWrongQuestion(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题目ID", required = true) @RequestParam Integer questionId) {
        return practiceService.addWrongQuestion(userId, questionId);
    }

    /**
     * 收藏题目
     * @param userId
     * @param questionId
     * @return
     */
    @PostMapping("/favoriteQuestion")
    @Operation(summary = "收藏题目")
    public Result<String> favoriteQuestion(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题目ID", required = true) @RequestParam Integer questionId) {
        return practiceService.favoriteQuestion(userId, questionId);
    }
}