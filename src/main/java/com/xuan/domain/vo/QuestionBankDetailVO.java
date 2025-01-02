package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题库详情VO")
public class QuestionBankDetailVO {

    @Schema(description = "题库ID")
    private Integer bankId;

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "单选题列表")
    private List<QuestionWithOrderVO> singleChoiceQuestions;

    @Schema(description = "多选题列表")
    private List<QuestionWithOrderVO> multipleChoiceQuestions;

    @Schema(description = "主观题列表")
    private List<QuestionWithOrderVO> subjectiveQuestions;
}