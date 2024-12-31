package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题目信息")
public class QuestionVO {
    @Schema(description = "题目ID")
    private Integer questionId;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "题目类型")
    private String type;

    @Schema(description = "创建者用户名")
    private String creatorName;

    @Schema(description = "题目答案列表")
    private List<AnswerVO> answers;
}