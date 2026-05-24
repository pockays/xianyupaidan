package com.xianyupaidan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("super_admin")
public class SuperAdmin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
