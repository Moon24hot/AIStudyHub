package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "StudyMaterialAddDTO", description = "学习资料新增DTO")
public class StudyMaterialAddDTO {
    @Schema(description = "用户id", required = true)
    private Integer userId;

    @Schema(description = "标题", required = true)
    private String title;

    @Schema(description = "内容", required = true)
    private String content;
}