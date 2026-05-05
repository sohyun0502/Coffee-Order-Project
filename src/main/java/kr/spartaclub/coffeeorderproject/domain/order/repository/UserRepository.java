package kr.spartaclub.coffeeorderproject.domain.order.repository;

import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
