package com.xianyupaidan.controller;

import com.xianyupaidan.common.Result;
import com.xianyupaidan.dto.LoginRequest;
import com.xianyupaidan.dto.LoginResponse;
import com.xianyupaidan.dto.XianyuLoginRequest;
import com.xianyupaidan.security.JwtTokenProvider;
import com.xianyupaidan.security.TenantContext;
import com.xianyupaidan.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/xianyu-login")
    public Result<LoginResponse> xianyuLogin(@Valid @RequestBody XianyuLoginRequest request) {
        return Result.ok(authService.xianyuLogin(request));
    }

    @PostMapping("/admin-login")
    public Result<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.adminLogin(request));
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Claims claims = jwtTokenProvider.parseToken(token);
        return Result.ok(Map.of(
                "userId", claims.get("userId", Long.class),
                "role", claims.get("role", String.class),
                "tenantId", claims.get("tenantId", String.class)
        ));
    }
}
