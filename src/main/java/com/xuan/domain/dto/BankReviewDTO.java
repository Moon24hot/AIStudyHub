package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BankReviewDTO", description = "题库审核DTO")
public class BankReviewDTO {

    @Schema(description = "题库ID", required = true)
    private Integer bankId;

    @Schema(description = "是否通过审核", required = true)
    private Boolean approved;
}