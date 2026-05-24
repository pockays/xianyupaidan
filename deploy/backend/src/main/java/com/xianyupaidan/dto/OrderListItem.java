package com.xianyupaidan.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListItem {
    private Long id;
    private Long userId;
    private String nickname;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
