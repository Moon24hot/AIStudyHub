package com.xuan.controller.user;

import com.xuan.domain.dto.QuestionAddDTO;
import com.xuan.domain.dto.QuestionUpdateDTO;
import com.xuan.domain.vo.QuestionVO;
import com.xuan.result.Result;
import com.xuan.service.IQuestionsService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "题目相关接口", description = "用户端 - 题目相关接口")
@RequestMapping("/api/user/question")
@RestController
public class QuestionController {

    @Autowired
    IQuestionsService questionsService;

//    /**
//     * 查询所有题目
//     * @return
//     */
//    @GetMapping("/list")
//    public List<Questions> list() {
//        return questionsService.list();
//    }

    /**
     * 新增题目
     *
     * @param questionAddDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增题目")
    public Result<String> addQuestion(@RequestBody QuestionAddDTO questionAddDTO) {
        return questionsService.addQuestion(questionAddDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "根据用户ID查询题目列表")
    public Result<List<QuestionVO>> getQuestionsByUserId(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        return questionsService.getQuestionsByUserId(userId);
    }


    /**
     * 根据题目ID修改题目
     * @param questionUpdateDTO
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "根据题目ID修改题目")
    public Result<String> updateQuestion(@RequestBody QuestionUpdateDTO questionUpdateDTO) {
        return questionsService.updateQuestion(questionUpdateDTO);
    }

    /**
     * 根据题目ID删除题目
     * @param userId
     * @param questionId
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "根据题目ID删除题目")
    public Result<String> deleteQuestion(
            @Parameter(description = "用户ID", required = true) @RequestParam Integer userId,
            @Parameter(description = "题目ID", required = true) @RequestParam Integer questionId) {
        return questionsService.deleteQuestion(userId, questionId);
    }
}