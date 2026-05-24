package com.xianyupaidan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tableName;
    private String operationType;
    private String recordId;
    private String oldData;
    private String newData;
    private String operator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
