package kr.spartaclub.coffeeorderproject.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompletedEvent {

    private Long orderId;
    private Long userId;
    private Long menuId;
    private Integer quantity;
    private Long price;
    private Long totalPrice;
    private String createdAt;
}
