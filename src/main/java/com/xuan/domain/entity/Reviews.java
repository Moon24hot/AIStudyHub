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
@TableName("reviews")
@Schema(name="Reviews对象", description="")
public class Reviews implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审核记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "待审核的题库ID")
    private Integer itemId;


}
