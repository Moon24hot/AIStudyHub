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
@TableName("admins")
@Schema(name="Admins对象", description="")
public class Admins implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "管理员ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "管理员手机号")
    private String phone;

    @Schema(description = "管理员用户名")
    private String username;

    @Schema(description = "管理员密码")
    private String password;


}
