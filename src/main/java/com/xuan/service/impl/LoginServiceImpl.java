package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.domain.dto.LoginDTO;
import com.xuan.domain.entity.Admins;
import com.xuan.domain.entity.Users;
import com.xuan.mapper.AdminsMapper;
import com.xuan.mapper.UsersMapper;
import com.xuan.result.Result;
import com.xuan.service.ILoginService;
import com.xuan.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private AdminsMapper adminsMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录
     *
     * @param loginDTO 登录信息
     * @return 结果
     */
    @Override
    public Result<String> login(LoginDTO loginDTO) {
        // 1. 检查参数
        if (StringUtils.isEmpty(loginDTO.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isEmpty(loginDTO.getPassword())) {
            return Result.error("密码不能为空");
        }
        if (StringUtils.isEmpty(loginDTO.getRole())) {
            return Result.error("角色类型不能为空");
        }

        // 2. 根据角色类型查询相应的表
        String userId = null;
        String username = loginDTO.getUsername();
        if ("user".equals(loginDTO.getRole())) {
            Users user = usersMapper.selectOne(
                    new LambdaQueryWrapper<Users>()
                            .eq(Users::getUsername, username)
            );
            if (user == null) {
                return Result.error("用户不存在");
            }
            if (!user.getPassword().equals(loginDTO.getPassword())) { // 实际应用中应对密码进行加密处理
                return Result.error("密码错误");
            }
            userId = String.valueOf(user.getId());
        } else if ("admin".equals(loginDTO.getRole())) {
            Admins admin = adminsMapper.selectOne(
                    new LambdaQueryWrapper<Admins>()
                            .eq(Admins::getUsername, username)
            );
            if (admin == null) {
                return Result.error("管理员不存在");
            }
            if (!admin.getPassword().equals(loginDTO.getPassword())) { // 实际应用中应对密码进行加密处理
                return Result.error("密码错误");
            }
            userId = String.valueOf(admin.getId());
        } else {
            return Result.error("角色类型错误");
        }

        // 3. 生成 JWT token
        String token = jwtUtil.generateToken(username, userId);

        return Result.success(token);
    }
}