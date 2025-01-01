package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "QuestionBankVO", description = "题库VO")
public class QuestionBankVO {

    @Schema(description = "题库ID")
    private Integer bankId;

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "题库包含的题目列表")
    private List<QuestionVO> questions;
}