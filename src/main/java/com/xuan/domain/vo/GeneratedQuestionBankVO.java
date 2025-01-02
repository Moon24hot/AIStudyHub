package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "GeneratedQuestionBankVO", description = "生成的题库VO")
public class GeneratedQuestionBankVO {

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "题库包含的题目列表")
    private List<QuestionVO> questions;
}