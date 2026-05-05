package kr.spartaclub.coffeeorderproject.domain.point.repository;

import kr.spartaclub.coffeeorderproject.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
