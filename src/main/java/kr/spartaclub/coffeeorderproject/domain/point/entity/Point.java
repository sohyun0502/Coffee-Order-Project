package kr.spartaclub.coffeeorderproject.domain.point.entity;

import jakarta.persistence.*;
import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Long balance;

    private LocalDateTime updatedAt;
}
