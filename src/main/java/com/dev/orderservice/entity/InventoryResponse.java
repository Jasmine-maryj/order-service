package com.dev.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class InventoryResponse {
    private String productName;
    private int quantity;
    private boolean isStock;
}
