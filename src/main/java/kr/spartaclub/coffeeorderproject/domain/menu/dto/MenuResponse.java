package kr.spartaclub.coffeeorderproject.domain.menu.dto;

import kr.spartaclub.coffeeorderproject.domain.menu.entity.Menu;

public record MenuResponse (
    Long menuId,
    String name,
    Long price
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice()
        );
    }
}
