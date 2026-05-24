package com.xianyupaidan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("preset_tag")
public class PresetTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tenantId;
    private String name;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
