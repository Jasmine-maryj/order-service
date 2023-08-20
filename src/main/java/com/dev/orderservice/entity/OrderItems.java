package com.dev.orderservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items_table")
@Entity
public class OrderItems {
    @Id
    @GeneratedValue
    private Long productId;
    private int quantity;
    private Double price;
}
