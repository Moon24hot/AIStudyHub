package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题库创建DTO")
public class QuestionBankCreateDTO {

    @Schema(description = "用户ID", required = true)
    private Integer userId;

    @Schema(description = "题库标题", required = true)
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "题目ID列表")
    private List<Integer> questionIds;
}