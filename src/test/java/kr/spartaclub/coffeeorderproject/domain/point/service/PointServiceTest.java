package kr.spartaclub.coffeeorderproject.domain.point.service;

import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import kr.spartaclub.coffeeorderproject.domain.order.repository.UserRepository;
import kr.spartaclub.coffeeorderproject.domain.point.dto.PointRequest;
import kr.spartaclub.coffeeorderproject.domain.point.entity.Point;
import kr.spartaclub.coffeeorderproject.domain.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointServiceTest {

    @Autowired private PointService pointService;
    @Autowired private PointRepository pointRepository;
    @Autowired private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(new User("유저1"));

        Point point = Point.builder()
                .user(savedUser)
                .balance(0L)
                .build();
        pointRepository.save(point);
    }

    @Test
    @DisplayName("동시에 10개 스레드에서 각 1,000원씩 충전하면, 최종 잔액은 10,000원이어야 한다.")
    void chargePoint_Concurrency_Test() throws InterruptedException {
        // Given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        PointRequest request = new PointRequest(savedUser.getId(), 1000L);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(request);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Then
        Point resultPoint = pointRepository.findByUserId(savedUser.getId()).orElseThrow();
        assertThat(resultPoint.getBalance()).isEqualTo(10000L);
    }
}