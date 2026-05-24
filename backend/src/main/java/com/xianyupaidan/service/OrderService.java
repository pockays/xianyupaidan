package com.xianyupaidan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.dto.*;
import com.xianyupaidan.entity.*;
import com.xianyupaidan.mapper.*;
import com.xianyupaidan.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderCategoryMapper orderCategoryMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserMapper userMapper;
    private final RabbitMQSender rabbitMQSender;
    private final SystemConfigMapper systemConfigMapper;

    @Transactional
    public Long createOrder(CreateOrderRequest request) {
        String tenantId = TenantContext.getTenantId();
        Long userId = TenantContext.getUserId();

        Order order = new Order();
        order.setTenantId(tenantId);
        order.setUserId(userId);
        order.setEmail(request.getEmail());
        order.setStatus("WAITING");
        order.setTotalPrice(BigDecimal.ZERO);
        order.setSubmitted(0);
        orderMapper.insert(order);

        if (request.getCategories() != null) {
            int catOrder = 0;
            for (CreateOrderRequest.CategoryData catData : request.getCategories()) {
                OrderCategory category = new OrderCategory();
                category.setOrderId(order.getId());
                category.setCategoryName(catData.getCategoryName());
                category.setSortOrder(catOrder++);
                orderCategoryMapper.insert(category);

                if (catData.getItems() != null) {
                    int itemOrder = 0;
                    for (CreateOrderRequest.CategoryData.ItemData itemData : catData.getItems()) {
                        if (itemData.getLinkUrl() == null && itemData.getNote() == null) {
                            continue;
                        }
                        OrderItem item = new OrderItem();
                        item.setCategoryId(category.getId());
                        item.setLinkUrl(itemData.getLinkUrl());
                        item.setNote(itemData.getNote());
                        item.setPrice(BigDecimal.ZERO);
                        item.setStatus("PENDING");
                        item.setSortOrder(itemOrder++);
                        orderItemMapper.insert(item);
                    }
                }
            }
        }
        return order.getId();
    }

    @Transactional
    public void updateOrder(Long orderId, CreateOrderRequest request) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("排单不存在");
        }
        if ("COMPLETED".equals(order.getStatus())) {
            throw new IllegalArgumentException("已完结的排单不可编辑");
        }
        if (!order.getUserId().equals(TenantContext.getUserId())
                && !"ADMIN".equals(TenantContext.getRole())) {
            throw new IllegalArgumentException("无权操作");
        }

        boolean isCurrent = "CURRENT".equals(order.getStatus());
        order.setEmail(request.getEmail());
        if (!isCurrent) {
            order.setSubmitted(1);
        }
        orderMapper.updateById(order);

        if (!isCurrent) {
            // WAITING: full edit — replace all categories and items
            List<OrderCategory> oldCats = orderCategoryMapper.selectList(
                    new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId));
            for (OrderCategory cat : oldCats) {
                orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getCategoryId, cat.getId()));
            }
            orderCategoryMapper.delete(new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId));
        }
        // CURRENT: only add new categories/items, don't delete existing

        if (request.getCategories() != null) {
            int catOrder = isCurrent ? orderCategoryMapper.selectList(
                    new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId)).size() : 0;
            for (CreateOrderRequest.CategoryData catData : request.getCategories()) {
                OrderCategory category = new OrderCategory();
                category.setOrderId(order.getId());
                category.setCategoryName(catData.getCategoryName());
                category.setSortOrder(catOrder++);
                orderCategoryMapper.insert(category);

                if (catData.getItems() != null) {
                    int itemOrder = 0;
                    for (CreateOrderRequest.CategoryData.ItemData itemData : catData.getItems()) {
                        if (itemData.getLinkUrl() == null && itemData.getNote() == null) {
                            continue;
                        }
                        OrderItem item = new OrderItem();
                        item.setCategoryId(category.getId());
                        item.setLinkUrl(itemData.getLinkUrl());
                        item.setNote(itemData.getNote());
                        item.setPrice(BigDecimal.ZERO);
                        item.setStatus("PENDING");
                        item.setSortOrder(itemOrder++);
                        orderItemMapper.insert(item);
                    }
                }
            }
        }
    }

    public void submitOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(TenantContext.getUserId())) {
            throw new IllegalArgumentException("无权操作");
        }
        order.setSubmitted(1);
        orderMapper.updateById(order);
    }

    public List<OrderListItem> getUserOrders() {
        Long userId = TenantContext.getUserId();
        String tenantId = TenantContext.getTenantId();
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getTenantId, tenantId)
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreatedAt));
        return orders.stream().map(this::toListItem).collect(Collectors.toList());
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("排单不存在");
        }
        String role = TenantContext.getRole();
        if ("USER".equals(role) && !order.getUserId().equals(TenantContext.getUserId())) {
            throw new IllegalArgumentException("无权查看");
        }
        return buildDetailResponse(order);
    }

    public List<OrderListItem> getAdminOrders(String status, String keyword,
                                               String startDate, String endDate,
                                               Boolean asc) {
        String tenantId = TenantContext.getTenantId();
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.eq(Order::getTenantId, tenantId);
        if (status != null && !status.isBlank()) {
            qw.eq(Order::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            List<User> users = userMapper.selectList(
                    new LambdaQueryWrapper<User>().like(User::getNickname, keyword));
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                qw.in(Order::getUserId, userIds);
            } else {
                qw.eq(Order::getUserId, -1L);
            }
        }
        if (startDate != null && !startDate.isBlank()) {
            qw.ge(Order::getCreatedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isBlank()) {
            qw.le(Order::getCreatedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        if (asc != null && asc) {
            qw.orderByAsc(Order::getCreatedAt);
        } else {
            qw.orderByDesc(Order::getCreatedAt);
        }
        return orderMapper.selectList(qw).stream().map(this::toListItem).collect(Collectors.toList());
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("排单不存在");
        }
        // Validate that it's the admin's tenant order (tenant filter handles this)
        String oldStatus = order.getStatus();
        order.setStatus(status);
        orderMapper.updateById(order);

        // Send email via RabbitMQ if status changed to CURRENT
        if ("CURRENT".equals(status) && !"CURRENT".equals(oldStatus)) {
            String email = order.getEmail();
            if (email != null && !email.isBlank()) {
                User user = userMapper.selectById(order.getUserId());
                rabbitMQSender.sendEmailNotification(email,
                        user != null ? user.getNickname() : "用户", "当前排单");
            }
            rabbitMQSender.sendStatusChange(orderId, oldStatus, status, order.getUserId());
        }
    }

    @Transactional
    public void addCategoriesToOrder(Long orderId, CreateOrderRequest request) {
        if (request.getCategories() != null) {
            int catOrder = orderCategoryMapper.selectList(
                    new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId)).size();
            for (CreateOrderRequest.CategoryData catData : request.getCategories()) {
                OrderCategory category = new OrderCategory();
                category.setOrderId(orderId);
                category.setCategoryName(catData.getCategoryName());
                category.setSortOrder(catOrder++);
                orderCategoryMapper.insert(category);
                if (catData.getItems() != null) {
                    int itemOrder = 0;
                    for (CreateOrderRequest.CategoryData.ItemData itemData : catData.getItems()) {
                        if (itemData.getLinkUrl() == null && itemData.getNote() == null) continue;
                        OrderItem item = new OrderItem();
                        item.setCategoryId(category.getId());
                        item.setLinkUrl(itemData.getLinkUrl());
                        item.setNote(itemData.getNote());
                        item.setPrice(BigDecimal.ZERO);
                        item.setStatus("PENDING");
                        item.setSortOrder(itemOrder++);
                        orderItemMapper.insert(item);
                    }
                }
            }
        }
    }

    public void addItemToOrder(Long orderId, AddItemRequest request) {
        OrderItem item = new OrderItem();
        item.setCategoryId(request.getCategoryId());
        item.setLinkUrl(request.getLinkUrl());
        item.setNote(request.getNote());
        item.setPrice(BigDecimal.ZERO);
        item.setStatus("PENDING");
        // Get max sort order
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getCategoryId, request.getCategoryId()));
        item.setSortOrder(items.size());
        orderItemMapper.insert(item);
    }

    public void updateOrderItem(Long orderId, Long itemId, UpdateItemRequest request) {
        OrderItem item = orderItemMapper.selectById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        if (request.getLinkUrl() != null) {
            item.setLinkUrl(request.getLinkUrl());
        }
        if (request.getNote() != null) {
            item.setNote(request.getNote());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        orderItemMapper.updateById(item);
        recalculateTotal(orderId);
    }

    public void deleteOrderItem(Long orderId, Long itemId) {
        orderItemMapper.deleteById(itemId);
        recalculateTotal(orderId);
    }

    @Transactional
    public void deleteUserOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(TenantContext.getUserId())) {
            throw new IllegalArgumentException("无权操作");
        }
        if (!"WAITING".equals(order.getStatus())) {
            throw new IllegalArgumentException("只能删除等待中的排单");
        }
        deleteOrder(orderId);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        List<OrderCategory> cats = orderCategoryMapper.selectList(
                new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId));
        for (OrderCategory cat : cats) {
            orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getCategoryId, cat.getId()));
        }
        orderCategoryMapper.delete(new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId));
        orderMapper.deleteById(orderId);
    }

    private void recalculateTotal(Long orderId) {
        List<OrderCategory> cats = orderCategoryMapper.selectList(
                new LambdaQueryWrapper<OrderCategory>().eq(OrderCategory::getOrderId, orderId));
        BigDecimal total = BigDecimal.ZERO;
        for (OrderCategory cat : cats) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getCategoryId, cat.getId()));
            for (OrderItem item : items) {
                if (item.getPrice() != null) {
                    total = total.add(item.getPrice());
                }
            }
        }
        Order order = orderMapper.selectById(orderId);
        order.setTotalPrice(total);
        orderMapper.updateById(order);
    }

    private OrderDetailResponse buildDetailResponse(Order order) {
        OrderDetailResponse resp = new OrderDetailResponse();
        resp.setId(order.getId());
        resp.setUserId(order.getUserId());
        resp.setEmail(order.getEmail());
        resp.setStatus(order.getStatus());
        resp.setTotalPrice(order.getTotalPrice());
        resp.setSubmitted(order.getSubmitted());
        resp.setCreatedAt(order.getCreatedAt());
        resp.setUpdatedAt(order.getUpdatedAt());

        User user = userMapper.selectById(order.getUserId());
        resp.setNickname(user != null ? user.getNickname() : "未知用户");

        List<OrderCategory> cats = orderCategoryMapper.selectList(
                new LambdaQueryWrapper<OrderCategory>()
                        .eq(OrderCategory::getOrderId, order.getId())
                        .orderByAsc(OrderCategory::getSortOrder));
        List<OrderDetailResponse.CategoryDetail> catList = new ArrayList<>();
        for (OrderCategory cat : cats) {
            OrderDetailResponse.CategoryDetail cd = new OrderDetailResponse.CategoryDetail();
            cd.setId(cat.getId());
            cd.setCategoryName(cat.getCategoryName());
            cd.setSortOrder(cat.getSortOrder());

            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getCategoryId, cat.getId())
                            .orderByAsc(OrderItem::getSortOrder));
            List<OrderDetailResponse.CategoryDetail.ItemDetail> itemList = items.stream().map(item -> {
                OrderDetailResponse.CategoryDetail.ItemDetail id = new OrderDetailResponse.CategoryDetail.ItemDetail();
                id.setId(item.getId());
                id.setLinkUrl(item.getLinkUrl());
                id.setNote(item.getNote());
                id.setPrice(item.getPrice());
                id.setStatus(item.getStatus());
                id.setSortOrder(item.getSortOrder());
                return id;
            }).collect(Collectors.toList());
            cd.setItems(itemList);
            catList.add(cd);
        }
        resp.setCategories(catList);
        return resp;
    }

    private OrderListItem toListItem(Order order) {
        OrderListItem item = new OrderListItem();
        item.setId(order.getId());
        item.setUserId(order.getUserId());
        item.setStatus(order.getStatus());
        item.setTotalPrice(order.getTotalPrice());
        item.setCreatedAt(order.getCreatedAt());
        User user = userMapper.selectById(order.getUserId());
        item.setNickname(user != null ? user.getNickname() : "未知用户");
        return item;
    }
}
