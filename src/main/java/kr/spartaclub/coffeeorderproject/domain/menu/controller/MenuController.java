package kr.spartaclub.coffeeorderproject.domain.menu.controller;

import kr.spartaclub.coffeeorderproject.common.response.ApiResponse;
import kr.spartaclub.coffeeorderproject.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorderproject.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    /**
     * 1. 커피 메뉴 목록 조회 API
     * @return
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenus () {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenus()));
    }
}
