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
@TableName("user_answers")
@Schema(name="UserAnswers对象", description="")
public class UserAnswers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "答题记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "题库ID")
    private Integer bankId;

    @Schema(description = "题目ID")
    private Integer questionId;

    @Schema(description = "选择的客观题选项")
    private String selectedOptions;

    @Schema(description = "主观题答案")
    private String subjectiveAnswer;


}
