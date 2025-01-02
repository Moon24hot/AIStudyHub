package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(name = "GenerateBankDtoRequirement", description = "AI生成的组题需求")
public class GenerateBankDtoRequirement {
    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "正面需求（需要的标签和数量）")
    private List<TagCount> positive_requirements;

    @Schema(description = "负面需求（不需要的标签和数量）")
    private List<TagCount> negative_requirements;

    @Data
    @Schema(name = "TagCount", description = "标签和数量")
    public static class TagCount {
        @Schema(description = "标签")
        private String tag;

        @Schema(description = "数量")
        private String count; // 保持为 String 类型
    }
}