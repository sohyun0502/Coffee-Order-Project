package kr.spartaclub.coffeeorderproject.domain.point.dto;

import kr.spartaclub.coffeeorderproject.domain.point.entity.Point;

public record PointResponse (
        Long userId,
        Long currentBalance
) {
    public static PointResponse from(Point point) {
        return new PointResponse(
                point.getUser().getId(),
                point.getBalance()
        );
    }
}
