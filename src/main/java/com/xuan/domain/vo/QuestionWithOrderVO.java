package com.xuan.domain.vo;

import com.xuan.domain.entity.Answers;
import com.xuan.domain.entity.QuestionTags;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "带序号的题目信息VO")
public class QuestionWithOrderVO {

    @Schema(description = "题目ID")
    private Integer questionId;

    @Schema(description = "题目序号")
    private Integer order;

    @Schema(description = "题目类型")
    private String type;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "题目答案列表")
    private List<Answers> answers;

    @Schema(description = "题目标签列表")
    private List<QuestionTags> tags;

    // 可以根据需要添加其他字段，例如解析、难度等
}