package com.dev.orderservice.service;

import com.dev.orderservice.dto.OrderDto;
import com.dev.orderservice.entity.InventoryResponse;
import com.dev.orderservice.entity.Order;
import com.dev.orderservice.entity.OrderItems;
import com.dev.orderservice.repository.OrderRepository;
import com.dev.orderservice.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    private static final String INVENTORY_SERVICE = "http://localhost:8084/api/v1/inventory";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public String placeOrder(OrderDto orderDto) {
        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItems> orderItemsList = orderDto.getOrderItemsList()
                .stream().map(this::mapToDto).toList();
        order.setOrderItemsList(orderItemsList);
        order.setUserId(orderDto.getUserId());
        order.setStatus(OrderStatus.CREATED);
        return "";

    }

    public Mono<String> checkInventoryAndPlaceOrder(String productName) {

        WebClient webClient = webClientBuilder.baseUrl(INVENTORY_SERVICE).build();

        return webClient.get()
                .uri("/{productName}", productName)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .flatMap(inventoryResponse -> {
                    if (inventoryResponse.isStock()) {
                        return Mono.just("Inventory is available. Proceed with order placement.");
                    } else {
                        return Mono.just("Inventory is not available. Cannot place order.");
                    }
                });
    }


    private OrderItems mapToDto(OrderItems orderItems) {
        OrderItems orderItems1 = new OrderItems();
        orderItems1.setQuantity(orderItems.getQuantity());
        orderItems1.setPrice(orderItems.getPrice());
        orderItems1.setProductName(orderItems.getProductName());
        return orderItems1;
    }

    private Mono<InventoryResponse> checkInventoryAvailability(String name) {
        WebClient webClient = webClientBuilder.baseUrl(INVENTORY_SERVICE).build();

        return webClient.get()
                .uri("/{name}", name)
                .retrieve()
                .bodyToMono(InventoryResponse.class);
    }
}
