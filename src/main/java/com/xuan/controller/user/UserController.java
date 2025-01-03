package com.xuan.controller.user;

import com.xuan.domain.vo.QuestionVO;
import com.xuan.result.Result;
import com.xuan.service.IFavoritesService;
import com.xuan.service.IWrongQuestionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "获取用户收藏的所有题目列表")
    public Result<List<QuestionVO>> getFavoriteQuestions(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        return favoritesService.getAllFavoriteQuestions(userId);
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

    @DeleteMapping("/wrongQuestion/remove")
    @Operation(summary = "将错题移出错题集")
    public Result<String> removeWrongQuestion(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题目ID", required = true) @RequestParam Integer questionId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        if (questionId == null) {
            return Result.error("题目ID不能为空");
        }
        return wrongQuestionsService.removeWrongQuestion(userId, questionId);
    }
}