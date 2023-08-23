package com.dev.orderservice.service;

import com.dev.orderservice.dto.OrderDto;
import com.dev.orderservice.dto.UpdateInventoryQuantityDto;
import com.dev.orderservice.entity.InventoryResponse;
import com.dev.orderservice.entity.Order;
import com.dev.orderservice.entity.OrderItems;
import com.dev.orderservice.repository.OrderRepository;
import com.dev.orderservice.utils.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

    private static final String INVENTORY_SERVICE = "http://localhost:8084/api/v1/inventory";
    private static final String ORDER_STATUS = "orderStatus";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Map<String, String> placeOrder(OrderDto orderDto) {
        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        //List of OrderItems
        List<OrderItems> orderItemsList = orderDto.getOrderItemsList()
                .stream().map(this::mapToDto).toList();

        order.setUserId(orderDto.getUserId());
        order.setStatus(OrderStatus.CREATED);

        //List of product names
        List<String> names = orderDto.getOrderItemsList().stream()
                .map(OrderItems::getProductName)
                .toList();

        Map<String, String> resultMap = null;

        try {
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://localhost:8084/api/v1/inventory/available-products",
                            uriBuilder -> uriBuilder.queryParam("names", names).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            assert inventoryResponseArray != null;

            List<Integer> requiredArr = orderDto.getOrderItemsList().stream().map(OrderItems::getQuantity).toList();

            log.info("List of orderItem quantities: {}", requiredArr);

            List<Integer> totalQuantity = new ArrayList<>(
                    Arrays.stream(inventoryResponseArray)
                            .map(response -> response.isStock() ? response.getQuantity() : 0)
                            .toList()
            );

            log.info("List of total quantities: {}", totalQuantity);

            resultMap = new HashMap<>();

            for (int i = 0; i < requiredArr.size(); i++) {
                int requiredQuantity = requiredArr.get(i);
                int availableQuantity = totalQuantity.get(i);
                String productName = orderDto.getOrderItemsList().get(i).getProductName();

                if (availableQuantity >= requiredQuantity) {
                    totalQuantity.set(i, availableQuantity - requiredQuantity);
                    resultMap.put(productName, "Product available, order placed successfully");
                } else {
                    resultMap.put(productName, "Product is out of stock");
                }
            }

            log.info("Remaining Product Quantity: {}", totalQuantity);

            boolean allProductsAvailable = resultMap.values().stream()
                    .allMatch(message -> message.equals("Product available, order placed successfully"));

            if (allProductsAvailable) {
                for (int i = 0; i < orderItemsList.size(); i++) {
                    OrderItems orderItem = orderItemsList.get(i);
                    orderItem.setQuantity(requiredArr.get(i));
                }

                order.setOrderItemsList(orderItemsList);
                orderRepository.save(order);

                updateInventory(names, totalQuantity);

                resultMap.put(ORDER_STATUS, "Order Placed Successfully");

            } else {
                resultMap.put(ORDER_STATUS, "Order could not be placed due to product availability");
            }
        } catch (Exception exception) {

            assert resultMap != null;
            resultMap.put(ORDER_STATUS, "Error while placing order: " + exception.getMessage());

        }
        return resultMap;

    }

    private void updateInventory(List<String> names, List<Integer> totalQuantity) {
        List<UpdateInventoryQuantityDto> updateInventoryQuantityDtos = new ArrayList<>();

        for(int i = 0; i < names.size(); i++){
            UpdateInventoryQuantityDto inventoryQuantityDto = new UpdateInventoryQuantityDto();
            inventoryQuantityDto.setName(names.get(i));
            inventoryQuantityDto.setQuantity(totalQuantity.get(i));
            updateInventoryQuantityDtos.add(inventoryQuantityDto);
        }

        WebClient webClient = webClientBuilder.baseUrl(INVENTORY_SERVICE).build();

        ResponseEntity<Map> map = webClient
                .put()
                .uri("/update")
                .body(BodyInserters.fromValue(updateInventoryQuantityDtos))
                .retrieve()
                .toEntity(Map.class)
                .block();

        assert map != null;
        if (map.getStatusCode().is2xxSuccessful()) {
            Map<String, String> responseBody = map.getBody();
            if (responseBody != null) {
                String message = responseBody.get("message");
                log.info("Update Inventory Stock status: {}", message);
            }
        } else {
            throw new RuntimeException("Inventory update failed");
        }
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
