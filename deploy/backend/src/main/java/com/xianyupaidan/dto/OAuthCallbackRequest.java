package com.xianyupaidan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthCallbackRequest {
    @NotBlank(message = "授权码不能为空")
    private String code;
    @NotBlank(message = "状态不能为空")
    private String state;
    private String sellerId;
}
