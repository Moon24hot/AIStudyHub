package com.xuan.controller.user;

import com.xuan.domain.entity.Questions;
import com.xuan.service.IQuestionsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "题目相关接口", description = "用户端 - 题目相关接口")
@RequestMapping("/api/user/question")
@RestController
public class QuestionController {

    @Autowired
    IQuestionsService questionsService;

    /**
     * 查询所有题目
     * @return
     */
    @GetMapping("/list")
    public List<Questions> list() {
        return questionsService.list();
    }

}