package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "StudyMaterialUpdateDTO", description = "学习资料更新DTO")
public class StudyMaterialUpdateDTO {

    @Schema(description = "学习资料ID", required = true)
    private Integer materialId;

    @Schema(description = "用户ID", required = true)
    private  Integer userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;
}