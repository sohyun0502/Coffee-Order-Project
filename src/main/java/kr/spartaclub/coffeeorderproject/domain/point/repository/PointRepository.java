package kr.spartaclub.coffeeorderproject.domain.point.repository;

import jakarta.persistence.LockModeType;
import kr.spartaclub.coffeeorderproject.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.user.id = :userId")
    Optional<Point> findByUserIdWithLock(@Param("userId") Long userId);

    Optional<Point> findByUserId(Long userId);
}
