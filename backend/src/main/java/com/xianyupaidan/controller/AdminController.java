package com.xianyupaidan.controller;

import com.xianyupaidan.common.Result;
import com.xianyupaidan.dto.*;
import com.xianyupaidan.entity.PresetTag;
import com.xianyupaidan.entity.SystemConfig;
import com.xianyupaidan.service.AdminService;
import com.xianyupaidan.service.OrderService;
import com.xianyupaidan.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final TagService tagService;
    private final AdminService adminService;

    @GetMapping("/orders")
    public Result<List<OrderListItem>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean asc) {
        return Result.ok(orderService.getAdminOrders(status, keyword, startDate, endDate, asc));
    }

    @GetMapping("/orders/{id}")
    public Result<OrderDetailResponse> getOrderDetail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    @PutMapping("/orders/{id}/status")
    public Result<?> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(id, request.getStatus());
        return Result.ok();
    }

    @PutMapping("/orders/{id}/items/{itemId}")
    public Result<?> updateItem(@PathVariable Long id, @PathVariable Long itemId,
                                 @RequestBody UpdateItemRequest request) {
        orderService.updateOrderItem(id, itemId, request);
        return Result.ok();
    }

    @PostMapping("/orders/{id}/items")
    public Result<?> addItem(@PathVariable Long id, @RequestBody AddItemRequest request) {
        orderService.addItemToOrder(id, request);
        return Result.ok();
    }

    @PostMapping("/orders/{id}/categories")
    public Result<?> addCategories(@PathVariable Long id, @RequestBody CreateOrderRequest request) {
        orderService.addCategoriesToOrder(id, request);
        return Result.ok();
    }

    @DeleteMapping("/orders/{id}/items/{itemId}")
    public Result<?> deleteItem(@PathVariable Long id, @PathVariable Long itemId) {
        orderService.deleteOrderItem(id, itemId);
        return Result.ok();
    }

    @PostMapping("/orders")
    public Result<Long> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(orderService.createOrder(request));
    }

    @DeleteMapping("/orders/{id}")
    public Result<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.ok();
    }

    @GetMapping("/tags")
    public Result<List<PresetTag>> getTags() {
        return Result.ok(tagService.getTags());
    }

    @PostMapping("/tags")
    public Result<PresetTag> createTag(@Valid @RequestBody TagRequest request) {
        return Result.ok(tagService.createTag(request.getName(), request.getSortOrder()));
    }

    @PutMapping("/tags/{id}")
    public Result<?> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        tagService.updateTag(id, request.getName(), request.getSortOrder());
        return Result.ok();
    }

    @DeleteMapping("/tags/{id}")
    public Result<?> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.ok();
    }

    @GetMapping("/config")
    public Result<SystemConfig> getConfig() {
        return Result.ok(adminService.getConfig());
    }

    @PutMapping("/config")
    public Result<?> updateConfig(@RequestBody SystemConfigRequest request) {
        adminService.updateConfig(request);
        return Result.ok();
    }
}
