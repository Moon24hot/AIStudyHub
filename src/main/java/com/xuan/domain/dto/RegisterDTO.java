package com.xuan.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "注册DTO")
public class RegisterDTO {

    @Schema(description = "用户名", required = true)
    private String username;

    @Schema(description = "密码", required = true)
    private String password;

    @Schema(description = "手机号")
    private String phone;
}