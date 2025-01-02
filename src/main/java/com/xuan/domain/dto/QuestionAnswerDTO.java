package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "题目答案DTO")
public class QuestionAnswerDTO {

    @Schema(description = "题目ID", required = true)
    private Integer questionId;

    @Schema(description = "客观题选项,多个选项用英文逗号隔开", example = "A,B,D")
    private String selectedOptions;

    @Schema(description = "主观题答案")
    private String subjectiveAnswer;
}