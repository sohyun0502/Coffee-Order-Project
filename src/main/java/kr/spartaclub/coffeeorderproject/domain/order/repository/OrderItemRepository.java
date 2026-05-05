package kr.spartaclub.coffeeorderproject.domain.order.repository;

import kr.spartaclub.coffeeorderproject.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
