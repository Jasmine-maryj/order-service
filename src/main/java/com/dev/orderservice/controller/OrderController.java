package com.dev.orderservice.controller;

import com.dev.orderservice.dto.OrderDto;
import com.dev.orderservice.entity.Order;
import com.dev.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDto orderDto){
        Order order = orderService.placeOrder(orderDto);
        return new ResponseEntity<>("Order Placed Successfully", HttpStatus.CREATED);
    }
}
