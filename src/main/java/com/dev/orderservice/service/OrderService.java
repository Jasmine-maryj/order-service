package com.dev.orderservice.service;

import com.dev.orderservice.dto.OrderDto;

import reactor.core.publisher.Mono;

import java.util.Map;


public interface OrderService {
    Map<String, String> placeOrder(OrderDto orderDto);

    Mono<String> checkInventoryAndPlaceOrder(String name);
}
