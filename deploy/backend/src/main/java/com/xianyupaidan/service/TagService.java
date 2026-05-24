package com.xianyupaidan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.entity.PresetTag;
import com.xianyupaidan.mapper.PresetTagMapper;
import com.xianyupaidan.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final PresetTagMapper presetTagMapper;

    public List<PresetTag> getTags() {
        return presetTagMapper.selectList(
                new LambdaQueryWrapper<PresetTag>()
                        .eq(PresetTag::getTenantId, TenantContext.getTenantId())
                        .orderByAsc(PresetTag::getSortOrder));
    }

    public PresetTag createTag(String name, Integer sortOrder) {
        PresetTag tag = new PresetTag();
        tag.setTenantId(TenantContext.getTenantId());
        tag.setName(name);
        tag.setSortOrder(sortOrder != null ? sortOrder : 0);
        presetTagMapper.insert(tag);
        return tag;
    }

    public void updateTag(Long id, String name, Integer sortOrder) {
        PresetTag tag = presetTagMapper.selectById(id);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在");
        }
        tag.setName(name);
        tag.setSortOrder(sortOrder);
        presetTagMapper.updateById(tag);
    }

    public void deleteTag(Long id) {
        presetTagMapper.deleteById(id);
    }
}
