package kr.spartaclub.coffeeorderproject.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.spartaclub.coffeeorderproject.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
