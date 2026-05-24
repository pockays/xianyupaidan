package com.xianyupaidan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tenantId;
    private String xianyuId;
    private String nickname;
    private String avatarUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
