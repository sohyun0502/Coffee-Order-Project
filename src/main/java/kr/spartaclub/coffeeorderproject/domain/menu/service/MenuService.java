package kr.spartaclub.coffeeorderproject.domain.menu.service;

import kr.spartaclub.coffeeorderproject.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorderproject.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorderproject.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    // 커피 메뉴 목록 조회
    @Cacheable(value = "menus", key = "'all'", cacheManager = "redisCacheManager")
    public List<MenuResponse> getMenus() {
        log.info("DB에서 메뉴 목록 조회");

        List<Menu> menus = menuRepository.findAll();
        return menus.stream()
                .map(MenuResponse::from)
                .toList();
    }
}
