package kr.spartaclub.coffeeorderproject.domain.order.service;

import kr.spartaclub.coffeeorderproject.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorderproject.domain.menu.repository.MenuRepository;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderRequest;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderResponse;
import kr.spartaclub.coffeeorderproject.domain.order.entity.Order;
import kr.spartaclub.coffeeorderproject.domain.order.entity.OrderItem;
import kr.spartaclub.coffeeorderproject.domain.order.entity.OrderStatus;
import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import kr.spartaclub.coffeeorderproject.domain.order.repository.OrderItemRepository;
import kr.spartaclub.coffeeorderproject.domain.order.repository.OrderRepository;
import kr.spartaclub.coffeeorderproject.domain.order.repository.UserRepository;
import kr.spartaclub.coffeeorderproject.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

    // 커피 주문/결제하기
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 없습니다."));

        pointService.usePoint(request.userId(), menu.getPrice());

        Order order = Order.builder()
                .user(user)
                .totalPrice(menu.getPrice() * request.quantity())
                .status(OrderStatus.COMPLETED)
                .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menu(menu)
                .quantity(request.quantity())
                .price(menu.getPrice())
                .build();
        orderItemRepository.save(orderItem);

        return OrderResponse.from(order, orderItem);
    }
}
