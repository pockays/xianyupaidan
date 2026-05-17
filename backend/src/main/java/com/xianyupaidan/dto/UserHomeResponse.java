package com.xianyupaidan.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserHomeResponse {
    private boolean orderEnabled;
    private String announcement;
    private long totalOrders;
    private long waitingOrders;
    private long currentOrders;
    private List<OrderListItem> recentOrders;
}
