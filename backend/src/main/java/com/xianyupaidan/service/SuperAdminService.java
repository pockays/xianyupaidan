package com.xianyupaidan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.dto.CreateAdminRequest;
import com.xianyupaidan.entity.Admin;
import com.xianyupaidan.mapper.AdminMapper;
import com.xianyupaidan.mapper.SystemConfigMapper;
import com.xianyupaidan.entity.SystemConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final AdminMapper adminMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final PasswordEncoder passwordEncoder;

    public List<Admin> getAllAdmins() {
        return adminMapper.selectList(null);
    }

    @Transactional
    public Admin createAdmin(CreateAdminRequest request) {
        // Check username unique
        if (adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, request.getUsername())) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        Admin admin = new Admin();
        admin.setTenantId(UUID.randomUUID().toString().replace("-", ""));
        admin.setUsername(request.getUsername());
        admin.setXianyuId(request.getXianyuId());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setEmail(request.getEmail());
        admin.setStatus(1);
        adminMapper.insert(admin);

        // Create default system config for this admin (tenant)
        SystemConfig config = new SystemConfig();
        config.setTenantId(admin.getTenantId());
        config.setOrderEnabled(1);
        config.setAnnouncement("");
        systemConfigMapper.insert(config);

        return admin;
    }

    @Transactional
    public void updateAdmin(Long id, CreateAdminRequest request) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }
        if (request.getUsername() != null) {
            admin.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            admin.setEmail(request.getEmail());
        }
        if (request.getXianyuId() != null) {
            admin.setXianyuId(request.getXianyuId());
        }
        adminMapper.updateById(admin);
    }

    public void deleteAdmin(Long id) {
        adminMapper.deleteById(id);
    }

    public void toggleAdminStatus(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }
        admin.setStatus(admin.getStatus() == 1 ? 0 : 1);
        adminMapper.updateById(admin);
    }
}
