package kr.spartaclub.coffeeorderproject.domain.menu.service;

import kr.spartaclub.coffeeorderproject.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorderproject.domain.menu.dto.RankingDto;
import kr.spartaclub.coffeeorderproject.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorderproject.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final StringRedisTemplate stringRedisTemplate;
    public static final String MENU_RANKING_DAILY_KEY = "menu:ranking:";

    // 커피 메뉴 목록 조회
    @Cacheable(value = "menus", key = "'all'", cacheManager = "redisCacheManager")
    public List<MenuResponse> getMenus() {
        log.info("DB에서 메뉴 목록 조회");

        List<Menu> menus = menuRepository.findAll();
        return menus.stream()
                .map(MenuResponse::from)
                .toList();
    }

    // 메뉴 순위 증가
    public void increaseMenuRanking(Long menuId, Integer quantity, LocalDate date) {
        String key = MENU_RANKING_DAILY_KEY + date.toString();
        stringRedisTemplate.opsForZSet().incrementScore(key, String.valueOf(menuId), quantity);
        stringRedisTemplate.expire(key, Duration.ofDays(8));
    }

    // 최근 7일간 인기있는 메뉴 3개를 조회
    public List<RankingDto> findMenuRankingTop3In7Days() {
        LocalDate currentDate = LocalDate.now();
        String destKey = MENU_RANKING_DAILY_KEY + "summary:" + currentDate;

        List<String> keysToUnion = IntStream.range(0, 7)
                .mapToObj(i -> MENU_RANKING_DAILY_KEY + currentDate.minusDays(i).toString())
                .toList();

        stringRedisTemplate.opsForZSet().unionAndStore(keysToUnion.get(0), keysToUnion.subList(1, 7), destKey);
        stringRedisTemplate.expire(destKey, Duration.ofMinutes(10));

        Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(destKey, 0, 2);

        if (result == null) {
            return Collections.emptyList();
        }

        return result.stream()
                .map(tuple -> new RankingDto(tuple.getValue(), tuple.getScore()))
                .toList();
    }
}
