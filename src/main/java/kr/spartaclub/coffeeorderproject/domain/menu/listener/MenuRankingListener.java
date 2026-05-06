package kr.spartaclub.coffeeorderproject.domain.menu.listener;

import kr.spartaclub.coffeeorderproject.domain.menu.service.MenuService;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuRankingListener {

    private final MenuService menuService;

    @KafkaListener(
            topics = "order-completed",
            groupId = "menu-ranking-group",
            containerFactory = "menuRankingKafkaListenerContainerFactory"
    )
    public void consume(OrderCompletedEvent event) {

        LocalDateTime createdAt = LocalDateTime.parse(event.getCreatedAt());
        LocalDate currentDate = createdAt.toLocalDate();
        menuService.increaseMenuRanking(event.getMenuId(), event.getQuantity(), currentDate);
    }
}
