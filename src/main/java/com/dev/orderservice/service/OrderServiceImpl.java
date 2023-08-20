package com.dev.orderservice.service;

import com.dev.orderservice.dto.OrderDto;
import com.dev.orderservice.entity.Order;
import com.dev.orderservice.entity.OrderItems;
import com.dev.orderservice.repository.OrderRepository;
import com.dev.orderservice.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order placeOrder(OrderDto orderDto) {
        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItems> orderItemsList = orderDto.getOrderItemsList()
                .stream().map(this::mapToDto).toList();
        order.setOrderItemsList(orderItemsList);

        order.setUserId(orderDto.getUserId());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return order;
    }

    private OrderItems mapToDto(OrderItems orderItems) {
        OrderItems orderItems1 = new OrderItems();
        orderItems1.setQuantity(orderItems.getQuantity());
        orderItems1.setPrice(orderItems.getPrice());
        orderItems1.setProductName(orderItems.getProductName());
        return orderItems1;
    }
}
