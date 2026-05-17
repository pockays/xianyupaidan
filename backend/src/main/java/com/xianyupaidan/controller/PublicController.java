package com.xianyupaidan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.common.Result;
import com.xianyupaidan.entity.PresetTag;
import com.xianyupaidan.mapper.AdminMapper;
import com.xianyupaidan.entity.SystemConfig;
import com.xianyupaidan.mapper.PresetTagMapper;
import com.xianyupaidan.mapper.SystemConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final SystemConfigMapper systemConfigMapper;
    private final AdminMapper adminMapper;
    private final PresetTagMapper presetTagMapper;

    @GetMapping("/announcement")
    public Result<String> getAnnouncement(@RequestParam(required = false) String tenantId) {
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getTenantId, tenantId));
        return Result.ok(config != null ? config.getAnnouncement() : "");
    }

    @GetMapping("/tags")
    public Result<List<PresetTag>> getTags(@RequestParam String tenantId) {
        return Result.ok(presetTagMapper.selectList(
                new LambdaQueryWrapper<PresetTag>()
                        .eq(PresetTag::getTenantId, tenantId)
                        .orderByAsc(PresetTag::getSortOrder)));
    }
}
