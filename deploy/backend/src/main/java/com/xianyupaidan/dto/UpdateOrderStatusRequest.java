package com.xianyupaidan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotBlank(message = "状态不能为空")
    private String status;
}
