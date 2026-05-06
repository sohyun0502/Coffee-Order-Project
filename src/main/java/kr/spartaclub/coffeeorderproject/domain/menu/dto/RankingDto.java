package kr.spartaclub.coffeeorderproject.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingDto {

    private String menuId;
    private double orderCount;
}

