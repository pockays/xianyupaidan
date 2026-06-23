package com.xianyupaidan.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateItemRequest {
    private String linkUrl;
    private String note;
    private String imageUrls;
    private BigDecimal price;
    private String status;
}
