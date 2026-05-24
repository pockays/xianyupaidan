package com.xianyupaidan.controller;

import com.xianyupaidan.common.Result;
import com.xianyupaidan.dto.*;
import com.xianyupaidan.security.TenantContext;
import com.xianyupaidan.service.OrderService;
import com.xianyupaidan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/home")
    public Result<UserHomeResponse> home() {
        return Result.ok(userService.getHome());
    }

    @PostMapping("/orders")
    public Result<Long> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(orderService.createOrder(request));
    }

    @GetMapping("/orders")
    public Result<List<OrderListItem>> getOrders() {
        return Result.ok(orderService.getUserOrders());
    }

    @GetMapping("/orders/{id}")
    public Result<OrderDetailResponse> getOrderDetail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    @PutMapping("/orders/{id}")
    public Result<?> updateOrder(@PathVariable Long id, @RequestBody CreateOrderRequest request) {
        orderService.updateOrder(id, request);
        return Result.ok();
    }

    @PostMapping("/orders/{id}/submit")
    public Result<?> submitOrder(@PathVariable Long id) {
        orderService.submitOrder(id);
        return Result.ok();
    }
}
