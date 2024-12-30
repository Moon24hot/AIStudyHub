package com.xuan.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answers")
@Schema(name="Answers对象", description="")
public class Answers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题目答案ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "题目ID")
    private Integer questionId;

    @Schema(description = "选项标识")
    private String optionLabel;

    @Schema(description = "选项对应的答案")
    private String optionContent;

    @Schema(description = "是否是正确答案")
    private Boolean isCorrect;

    @Schema(description = "主观题答案")
    private String subjectiveAnswer;


}
