package com.xuan.controller.login;

import com.xuan.domain.dto.LoginDTO;
import com.xuan.domain.dto.RegisterDTO;
import com.xuan.result.Result;
import com.xuan.service.ILoginService;
import com.xuan.service.IUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "登录/注册相关接口", description = "登录/注册相关接口")
public class LoginController {

    @Autowired
    private IUsersService userService;

    @Autowired
    private ILoginService loginService;

    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * 用户/管理员登录
     * @param loginDTO
     * @return
     */
    @PostMapping("/auth")
    @Operation(summary = "用户/管理员登录")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }
}
