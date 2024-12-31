package com.xuan.domain.dto;

import lombok.Data;

@Data
public class AnswerAddDTO {
    private String optionLabel; // 选项标识
    private String optionContent; // 选项内容
    private Boolean isCorrect; // 是否正确答案
    private String subjectiveAnswer; // 主观题答案
}