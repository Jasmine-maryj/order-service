package com.dev.orderservice.controller;

import com.dev.orderservice.dto.OrderDto;
import com.dev.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody OrderDto orderDto){
        Map<String, String> result = orderService.placeOrder(orderDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

//    @GetMapping("/{name}")
//    public Mono<ResponseEntity<String>> checkInventoryAndPlaceOrder(@PathVariable String name) {
//        return orderService.checkInventoryAndPlaceOrder(name)
//                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
//                .defaultIfEmpty(new ResponseEntity<>("Inventory not available", HttpStatus.BAD_REQUEST));
//    }
}
