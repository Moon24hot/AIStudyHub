package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.domain.dto.RegisterDTO;
import com.xuan.domain.entity.Users;
import com.xuan.mapper.UsersMapper;
import com.xuan.result.Result;
import com.xuan.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-12-30
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    private UsersMapper usersMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> register(RegisterDTO registerDTO) {
        // 1. 检查参数
        if (StringUtils.isEmpty(registerDTO.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isEmpty(registerDTO.getPassword())) {
            return Result.error("密码不能为空");
        }

        // 2. 检查用户名是否已存在
        Users existingUser = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getUsername, registerDTO.getUsername())
        );
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 3. 注册新用户
        Users newUser = new Users();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(registerDTO.getPassword()); // 实际应用中应对密码进行加密处理
        newUser.setPhone(registerDTO.getPhone());
        usersMapper.insert(newUser);

        return Result.success("注册成功");
    }
}
