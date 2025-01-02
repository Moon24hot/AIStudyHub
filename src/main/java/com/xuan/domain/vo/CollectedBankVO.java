package com.xuan.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "收藏题库信息VO")
public class CollectedBankVO {

    @Schema(description = "题库ID")
    private Integer bankId;

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "创建者名字")
    private String creatorName;
}