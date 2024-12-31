package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "答案更新DTO")
public class AnswerUpdateDTO {

    @Schema(description = "答案ID")
    private Integer answerId;

    @Schema(description = "选项内容")
    private String optionContent;

    @Schema(description = "是否正确答案")
    private Boolean isCorrect;
}