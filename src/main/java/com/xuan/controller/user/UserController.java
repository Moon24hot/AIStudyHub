package com.xuan.controller.user;

import com.xuan.domain.vo.QuestionBankVO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.result.Result;
import com.xuan.service.IFavoritesService;
import com.xuan.service.IWrongQuestionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "个人中心相关接口", description = "用户端 - 个人中心相关接口")
@RequestMapping("/api/user/profile")
@RestController
public class UserController {

    @Autowired
    private IFavoritesService favoritesService;

    @Autowired
    private IWrongQuestionsService wrongQuestionsService;

    @GetMapping("/favorites/questions")
    @Operation(summary = "获取用户收藏的题目列表")
    public Result<List<QuestionVO>> getFavoriteQuestions(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        return favoritesService.getFavoriteQuestions(userId);
    }

    @GetMapping("/favorites/banks")
    @Operation(summary = "获取用户收藏的题库列表")
    public Result<List<QuestionBankVO>> getFavoriteQuestionBanks(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        return favoritesService.getFavoriteQuestionBanks(userId);
    }

    @GetMapping("/wrongQuestions")
    @Operation(summary = "获取用户错题列表")
    public Result<List<QuestionVO>> getWrongQuestions(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        return wrongQuestionsService.getWrongQuestions(userId);
    }

}