package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户答案DTO")
public class UserAnswerDTO {

    @Schema(description = "用户ID", required = true)
    private Integer userId;

    @Schema(description = "题库ID", required = true)
    private Integer bankId;

    @Schema(description = "题目答案列表", required = true)
    private List<QuestionAnswerDTO> questionAnswers;
}