package kr.spartaclub.coffeeorderproject.domain.order.dto;

public record OrderRequest(
        Long userId,
        Long menuId,
        Integer quantity
) {
}