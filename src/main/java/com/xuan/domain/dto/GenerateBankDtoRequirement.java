package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
public class GenerateBankDtoRequirement {
    private String title;
    private String description;
    private List<TagCount> positive_requirements;
    private List<TagCount> negative_requirements;

    @Data
    public static class TagCount {
        private String tag;
        private String count;
    }
}