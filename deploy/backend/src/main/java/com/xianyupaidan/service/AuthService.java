package com.xianyupaidan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.dto.LoginRequest;
import java.util.Map;
import com.xianyupaidan.dto.LoginResponse;
import com.xianyupaidan.dto.XianyuLoginRequest;
import com.xianyupaidan.entity.*;
import com.xianyupaidan.mapper.*;
import com.xianyupaidan.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SuperAdminMapper superAdminMapper;
    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TaobaoOAuthService taobaoOAuthService;

    public LoginResponse xianyuLogin(XianyuLoginRequest request) {
        // Find admin by username (sellerId)
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, request.getSellerId()));
        if (admin == null) {
            throw new IllegalArgumentException("卖家不存在，请检查卖家ID");
        }
        if (admin.getStatus() == 0) {
            throw new IllegalArgumentException("该卖家已暂停接单");
        }
        String tenantId = admin.getTenantId();

        // Find or create user under this tenant
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, tenantId)
                .eq(User::getXianyuId, request.getXianyuId()));
        if (user == null) {
            user = new User();
            user.setXianyuId(request.getXianyuId());
            user.setNickname(request.getXianyuId());
            user.setTenantId(tenantId);
            userMapper.insert(user);
        }
        String token = jwtTokenProvider.createToken(user.getId(), "USER", user.getTenantId());
        return new LoginResponse(token, "USER", user.getNickname(), user.getTenantId());
    }

    public Map<String, String> getOAuthAuthorizeUrl(String state) {
        return Map.of("url", taobaoOAuthService.getAuthorizeUrl(state));
    }

    public LoginResponse oauthUserLogin(String code, String state, String sellerId) {
        Map<String, Object> userInfo = taobaoOAuthService.exchangeCode(code);
        String taobaoUserId = (String) userInfo.get("taobaoUserId");
        String taobaoUserNick = (String) userInfo.get("taobaoUserNick");

        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, sellerId));
        if (admin == null) {
            throw new IllegalArgumentException("卖家不存在，请检查卖家ID");
        }
        if (admin.getStatus() == 0) {
            throw new IllegalArgumentException("该卖家已暂停接单");
        }
        String tenantId = admin.getTenantId();

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, tenantId)
                .eq(User::getXianyuId, taobaoUserId));
        if (user == null) {
            user = new User();
            user.setXianyuId(taobaoUserId);
            user.setNickname(taobaoUserNick);
            user.setTenantId(tenantId);
            userMapper.insert(user);
        }
        String token = jwtTokenProvider.createToken(user.getId(), "USER", user.getTenantId());
        return new LoginResponse(token, "USER", user.getNickname(), user.getTenantId());
    }

    public LoginResponse oauthAdminLogin(String code, String state) {
        Map<String, Object> userInfo = taobaoOAuthService.exchangeCode(code);
        String taobaoUserId = (String) userInfo.get("taobaoUserId");

        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getXianyuId, taobaoUserId));
        if (admin == null) {
            throw new IllegalArgumentException("该闲鱼账号未绑定管理员，请联系超级管理员");
        }
        if (admin.getStatus() == 0) {
            throw new IllegalArgumentException("账户已被禁用");
        }
        String token = jwtTokenProvider.createToken(admin.getId(), "ADMIN", admin.getTenantId());
        return new LoginResponse(token, "ADMIN", admin.getUsername(), admin.getTenantId());
    }

    public LoginResponse adminLogin(LoginRequest request) {
        // Try super admin first
        SuperAdmin sa = superAdminMapper.selectOne(new LambdaQueryWrapper<SuperAdmin>()
                .eq(SuperAdmin::getUsername, request.getUsername()));
        if (sa != null && passwordEncoder.matches(request.getPassword(), sa.getPasswordHash())) {
            String token = jwtTokenProvider.createToken(sa.getId(), "SUPER_ADMIN", "");
            return new LoginResponse(token, "SUPER_ADMIN", sa.getUsername(), "");
        }
        // Try admin
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, request.getUsername()));
        if (admin == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new IllegalArgumentException("账户已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        String token = jwtTokenProvider.createToken(admin.getId(), "ADMIN", admin.getTenantId());
        return new LoginResponse(token, "ADMIN", admin.getUsername(), admin.getTenantId());
    }
}
