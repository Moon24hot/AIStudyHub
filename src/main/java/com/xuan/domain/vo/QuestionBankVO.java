package com.xuan.domain.vo;

import com.xuan.enums.BanksStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "题库信息VO")
public class QuestionBankVO {

    @Schema(description = "题库ID")
    private Integer bankId;

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "状态")
    private BanksStatus status;
}