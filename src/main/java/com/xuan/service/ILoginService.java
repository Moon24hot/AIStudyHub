package com.xuan.service;

import com.xuan.domain.dto.LoginDTO;
import com.xuan.result.Result;

public interface ILoginService {
    /**
     * 登录
     *
     * @param loginDTO 登录信息
     * @return 结果
     */
    Result<String> login(LoginDTO loginDTO);
}