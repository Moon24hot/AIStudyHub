package com.xuan.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
@TableName("questions")
@Schema(name="Questions对象", description="")
public class Questions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题目ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "题目类型（单选题、多选题、主观题）")
    private String type;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "题目的创建者ID")
    private Integer creatorId;

    @Schema(description = "题目的创建时间")
    private LocalDateTime createTime;


}