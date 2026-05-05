package kr.spartaclub.coffeeorderproject.domain.point.dto;

public record PointRequest(
        Long userId,
        Long amount
) {
}