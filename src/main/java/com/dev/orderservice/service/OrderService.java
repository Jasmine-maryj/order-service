package com.dev.orderservice.service;

import com.dev.orderservice.dto.OrderDto;
import com.dev.orderservice.entity.Order;

public interface OrderService {
    Order placeOrder(OrderDto orderDto);
}
