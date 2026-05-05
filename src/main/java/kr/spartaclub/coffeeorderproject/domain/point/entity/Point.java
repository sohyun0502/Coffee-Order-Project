package kr.spartaclub.coffeeorderproject.domain.point.entity;

import jakarta.persistence.*;
import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    public Point(User user, Long balance) {
        this.user = user;
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }

    public void addBalance(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0원보다 커야 합니다.");
        }
        this.balance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void useBalance(Long amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        this.balance -= amount;
        this.updatedAt = LocalDateTime.now();
    }
}
