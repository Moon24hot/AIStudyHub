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
@TableName("favorites")
@Schema(name="Favorites对象", description="")
public class Favorites implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "收藏夹所属用户的ID")
    private Integer userId;

    @Schema(description = "被收藏的题库/题目ID")
    private Integer itemId;

    @Schema(description = "类型（题库、题目）")
    private String type;


}
