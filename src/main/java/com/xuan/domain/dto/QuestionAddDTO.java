package com.xuan.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionAddDTO {
    private Integer userId;
    private String type; // 题目类型（单选题、多选题、主观题）
    private String content; // 题目内容
    private List<AnswerAddDTO> answers; // 答案列表
    private List<String> tags; // 标签列表
}