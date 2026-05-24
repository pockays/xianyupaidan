package com.xianyupaidan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class XianyuLoginRequest {
    @NotBlank(message = "闲鱼ID不能为空")
    private String xianyuId;
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;
}
