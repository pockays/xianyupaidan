package com.xianyupaidan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianyupaidan.dto.OrderListItem;
import com.xianyupaidan.dto.UserHomeResponse;
import com.xianyupaidan.entity.*;
import com.xianyupaidan.mapper.*;
import com.xianyupaidan.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SystemConfigMapper systemConfigMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public UserHomeResponse getHome() {
        String tenantId = TenantContext.getTenantId();
        Long userId = TenantContext.getUserId();

        UserHomeResponse resp = new UserHomeResponse();
        resp.setOrderEnabled(true);
        resp.setAnnouncement(null);

        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getTenantId, tenantId));
        if (config != null) {
            resp.setOrderEnabled(config.getOrderEnabled() == null || config.getOrderEnabled() == 1);
            resp.setAnnouncement(config.getAnnouncement());
        }

        List<Order> allOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>().eq(Order::getTenantId, tenantId));
        resp.setTotalOrders(allOrders.size());
        resp.setWaitingOrders(allOrders.stream().filter(o -> "WAITING".equals(o.getStatus())).count());
        resp.setCurrentOrders(allOrders.stream().filter(o -> "CURRENT".equals(o.getStatus())).count());

        List<Order> recentOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getTenantId, tenantId)
                        .orderByDesc(Order::getCreatedAt)
                        .last("LIMIT 10"));
        resp.setRecentOrders(recentOrders.stream().map(o -> {
            OrderListItem item = new OrderListItem();
            item.setId(o.getId());
            item.setUserId(o.getUserId());
            item.setStatus(o.getStatus());
            item.setTotalPrice(o.getTotalPrice());
            item.setCreatedAt(o.getCreatedAt());
            User u = userMapper.selectById(o.getUserId());
            item.setNickname(u != null ? u.getNickname() : "未知用户");
            return item;
        }).collect(Collectors.toList()));

        return resp;
    }
}
