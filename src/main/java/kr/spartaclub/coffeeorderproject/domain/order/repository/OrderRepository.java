package kr.spartaclub.coffeeorderproject.domain.order.repository;

import kr.spartaclub.coffeeorderproject.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
