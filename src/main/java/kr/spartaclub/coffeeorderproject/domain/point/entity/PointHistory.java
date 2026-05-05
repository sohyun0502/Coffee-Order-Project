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
@Table(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private PointStatus type;

    private LocalDateTime createdAt;

    @Builder
    public PointHistory(User user, Long amount, PointStatus type) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }
}
