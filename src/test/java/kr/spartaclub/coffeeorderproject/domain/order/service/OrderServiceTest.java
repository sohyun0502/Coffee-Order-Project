package kr.spartaclub.coffeeorderproject.domain.order.service;

import kr.spartaclub.coffeeorderproject.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorderproject.domain.menu.repository.MenuRepository;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderCompletedEvent;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderRequest;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderResponse;
import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import kr.spartaclub.coffeeorderproject.domain.order.producer.OrderProducer;
import kr.spartaclub.coffeeorderproject.domain.order.repository.UserRepository;
import kr.spartaclub.coffeeorderproject.domain.point.entity.Point;
import kr.spartaclub.coffeeorderproject.domain.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private PointRepository pointRepository;

    @SpyBean
    private OrderProducer orderProducer; // Kafka 전송 여부 확인용

    private User savedUser;
    private Menu savedMenu;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder().name("주문자").build());
        savedMenu = menuRepository.save(Menu.builder().name("아메리카노").price(4500L).build());

        // 초기 포인트 충전 (10,000원)
        pointRepository.save(Point.builder().user(savedUser).balance(10000L).build());
    }

    @Test
    @DisplayName("정상적인 주문 요청 시, 포인트가 차감되고 주문 내역이 저장되며 Kafka로 전송된다.")
    void createOrder_Success_Test() {
        // Given
        OrderRequest request = new OrderRequest(savedUser.getId(), savedMenu.getId(), 1);

        // When
        OrderResponse response = orderService.createOrder(request);

        // Then
        // 1. 응답값 검증
        assertThat(response.orderId()).isNotNull();
        assertThat(response.totalPrice()).isEqualTo(4500L);

        // 2. 포인트 차감 검증 (10,000 - 4,500 = 5,500)
        Point point = pointRepository.findByUserId(savedUser.getId()).get();
        assertThat(point.getBalance()).isEqualTo(5500L);

        // 3. Kafka 전송 검증 (BDDMockito 사용)
        then(orderProducer).should(times(1)).send(any(OrderCompletedEvent.class));
    }

    @Test
    @DisplayName("동일 사용자가 동시에 주문을 2번 요청하면, 분산 락에 의해 하나만 성공해야 한다.")
    void createOrder_Concurrency_Lock_Test() throws InterruptedException {
        // Given
        OrderRequest request = new OrderRequest(savedUser.getId(), savedMenu.getId(), 1);
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    orderService.createOrder(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("주문 실패 사유: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        // 한 번의 주문만 성공하고, 나머지는 락 획득 실패 혹은 잔액 부족 등으로 실패해야 함
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}