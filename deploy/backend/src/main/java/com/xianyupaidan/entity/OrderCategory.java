package com.xianyupaidan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("order_category")
public class OrderCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String categoryName;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
