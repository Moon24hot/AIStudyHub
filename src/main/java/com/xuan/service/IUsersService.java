package com.xuan.service;

import com.xuan.domain.dto.RegisterDTO;
import com.xuan.domain.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.result.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
public interface IUsersService extends IService<Users> {
    /**
     * 注册
     *
     * @param registerDTO 注册信息
     * @return 结果
     */
    Result<String> register(RegisterDTO registerDTO);
}
