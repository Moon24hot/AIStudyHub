package com.xuan.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "做题相关接口", description = "用户端 - 做题相关接口")
@RequestMapping("/api/user/practice")
@RestController
public class PracticeController {

}