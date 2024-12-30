package com.xuan.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "我的题库/收藏题库相关接口", description = "用户端 - 我的题库/收藏题库相关接口")
@RequestMapping("/api/user/bank")
@RestController
public class BankController {

}