package com.xuan.controller.user;

import com.xuan.domain.dto.QuestionAddDTO;
import com.xuan.domain.entity.Questions;
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
     * @param questionAddDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增题目")
    public Result<String> addQuestion(@RequestBody QuestionAddDTO questionAddDTO) {
        return questionsService.addQuestion(questionAddDTO);
    }

}