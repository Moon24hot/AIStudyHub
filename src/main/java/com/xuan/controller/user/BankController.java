package com.xuan.controller.user;

import com.xuan.domain.entity.QuestionBanks;
import com.xuan.service.IQuestionBanksService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "我的题库/收藏题库相关接口", description = "用户端 - 我的题库/收藏题库相关接口")
@RequestMapping("/api/user/bank")
@RestController
public class BankController {

    @Autowired
    private IQuestionBanksService questionBanksService;

    /**
     * 查询所有题库
     * @return
     */
    @GetMapping("/list")
    public List<QuestionBanks> getAllBanks(){
        return questionBanksService.list();
    }
}