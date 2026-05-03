package kr.spartaclub.coffeeorderproject.domain.point.repository;

import kr.spartaclub.coffeeorderproject.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
