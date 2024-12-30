package com.xuan.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "题库审核相关接口", description = "管理员端 - 题库审核相关接口")
@RequestMapping("/api/admin/review")
@RestController
public class BankReviewController {

}