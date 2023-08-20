package com.dev.orderservice.dto;

import com.dev.orderservice.entity.OrderItems;
import com.dev.orderservice.utils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
   private Long userId;
   private List<OrderItems> orderItemsList;
}
