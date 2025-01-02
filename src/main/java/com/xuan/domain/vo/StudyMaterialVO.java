package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "StudyMaterialVO", description = "学习资料VO")
public class StudyMaterialVO {

    @Schema(description = "学习资料ID")
    private Integer id;

    @Schema(description = "创建者ID")
    private Integer creatorId;

    @Schema(description = "标题")
    private String title;
}