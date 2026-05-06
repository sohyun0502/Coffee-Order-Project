package kr.spartaclub.coffeeorderproject.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCompletedEvent {
    private final Long userId;
    private final Long menuId;
    private final Long amount;
}
