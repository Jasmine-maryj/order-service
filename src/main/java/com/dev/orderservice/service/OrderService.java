package com.dev.orderservice.service;

import com.dev.orderservice.dto.OrderDto;

import reactor.core.publisher.Mono;


public interface OrderService {
    String placeOrder(OrderDto orderDto);

    Mono<String> checkInventoryAndPlaceOrder(String name);
}
