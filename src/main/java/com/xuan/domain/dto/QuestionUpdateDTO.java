package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题目更新DTO")
public class QuestionUpdateDTO {

    @Schema(description = "用户ID", required = true)
    private Integer userId;

    @Schema(description = "题目ID", required = true)
    private Integer questionId;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "题目类型")
    private String type;

    @Schema(description = "客观题答案更新列表")
    private List<AnswerUpdateDTO> answers;

    @Schema(description = "主观题答案")
    private String subjectiveAnswer;
}