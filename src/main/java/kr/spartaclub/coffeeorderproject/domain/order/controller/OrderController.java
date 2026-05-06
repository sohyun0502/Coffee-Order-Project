package kr.spartaclub.coffeeorderproject.domain.order.controller;

import kr.spartaclub.coffeeorderproject.common.response.ApiResponse;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderRequest;
import kr.spartaclub.coffeeorderproject.domain.order.dto.OrderResponse;
import kr.spartaclub.coffeeorderproject.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 3. 커피 주문/결제 하기 API
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(orderService.createOrder(request)));
    }
}
