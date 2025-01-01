package com.xuan.domain.vo;

import com.xuan.enums.BanksStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "BankReviewVO", description = "题库审核信息VO")
public class BankReviewVO {

    @Schema(description = "题库ID")
    private Integer bankId;

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "审核状态")
    private BanksStatus status;

    @Schema(description = "选择题列表")
    private List<QuestionVO> choiceQuestions;

    @Schema(description = "主观题列表")
    private List<QuestionVO> subjectiveQuestions;
}