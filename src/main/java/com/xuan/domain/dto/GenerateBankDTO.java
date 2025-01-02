package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "GenerateBankDTO", description = "生成题库DTO")
public class GenerateBankDTO {

    @Schema(description = "用户ID", required = true)
    private Integer userId;

    @Schema(description = "题库需求", required = true)
    private String requirement;
}