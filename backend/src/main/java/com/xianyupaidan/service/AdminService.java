package com.xianyupaidan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.dto.SystemConfigRequest;
import com.xianyupaidan.entity.SystemConfig;
import com.xianyupaidan.mapper.SystemConfigMapper;
import com.xianyupaidan.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SystemConfigMapper systemConfigMapper;

    public SystemConfig getConfig() {
        String tenantId = TenantContext.getTenantId();
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getTenantId, tenantId));
        if (config == null) {
            config = new SystemConfig();
            config.setTenantId(tenantId);
            config.setOrderEnabled(1);
            config.setAnnouncement("");
            systemConfigMapper.insert(config);
        }
        return config;
    }

    public void updateConfig(SystemConfigRequest request) {
        String tenantId = TenantContext.getTenantId();
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getTenantId, tenantId));
        if (config == null) {
            config = new SystemConfig();
            config.setTenantId(tenantId);
        }
        if (request.getOrderEnabled() != null) {
            config.setOrderEnabled(request.getOrderEnabled());
        }
        if (request.getAnnouncement() != null) {
            config.setAnnouncement(request.getAnnouncement());
        }
        systemConfigMapper.insertOrUpdate(config);
    }
}
