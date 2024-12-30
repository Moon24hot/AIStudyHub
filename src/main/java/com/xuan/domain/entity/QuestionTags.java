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
@TableName("question_tags")
@Schema(name="QuestionTags对象", description="")
public class QuestionTags implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题目标签ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "题目ID")
    private Integer questionId;


}
