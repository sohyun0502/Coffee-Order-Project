package kr.spartaclub.coffeeorderproject.domain.order.dto;

import kr.spartaclub.coffeeorderproject.domain.order.entity.Order;
import kr.spartaclub.coffeeorderproject.domain.order.entity.OrderItem;

import java.time.LocalDateTime;

public record OrderResponse (
        Long orderId,
        Long userId,
        Long menuId,
        Integer quantity,
        Long price,
        Long totalPrice,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order, OrderItem orderItem) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                orderItem.getMenu().getId(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
