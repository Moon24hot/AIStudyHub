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
@TableName("question_banks")
@Schema(name="QuestionBanks对象", description="")
public class QuestionBanks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题库ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "题库创建者的ID")
    private Integer creatorId;

    @Schema(description = "题库标题")
    private String title;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "状态（0未分享 1审核中 2被拒绝 3已公开）")
    private Integer status;

    @Schema(description = "题库的创建时间")
    private LocalDateTime createTime;


}
