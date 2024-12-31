package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "答案信息")
public class AnswerVO {
    @Schema(description = "答案ID")
    private Integer answerId;

    @Schema(description = "选项标识")
    private String optionLabel;

    @Schema(description = "选项内容")
    private String optionContent;

    @Schema(description = "是否正确答案")
    private Boolean isCorrect;

    @Schema(description = "主观题答案")
    private String subjectiveAnswer;
}