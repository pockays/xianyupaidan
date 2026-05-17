package com.xianyupaidan.dto;

import lombok.Data;

@Data
public class AddItemRequest {
    private Long categoryId;
    private String linkUrl;
    private String note;
}
