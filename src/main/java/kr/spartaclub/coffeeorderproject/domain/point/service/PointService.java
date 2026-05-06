package kr.spartaclub.coffeeorderproject.domain.point.service;

import kr.spartaclub.coffeeorderproject.domain.order.entity.User;
import kr.spartaclub.coffeeorderproject.domain.order.repository.UserRepository;
import kr.spartaclub.coffeeorderproject.domain.point.dto.PointRequest;
import kr.spartaclub.coffeeorderproject.domain.point.dto.PointResponse;
import kr.spartaclub.coffeeorderproject.domain.point.entity.Point;
import kr.spartaclub.coffeeorderproject.domain.point.entity.PointHistory;
import kr.spartaclub.coffeeorderproject.domain.point.entity.PointStatus;
import kr.spartaclub.coffeeorderproject.domain.point.repository.PointHistoryRepository;
import kr.spartaclub.coffeeorderproject.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    // 포인트 충전하기
    @Transactional
    public PointResponse chargePoint(PointRequest request) {

        if (request.amount() <= 0) {
            throw new IllegalArgumentException("0이상 입력하세요.");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        Point point = pointRepository.findByUserIdWithLock(request.userId())
                .orElseGet(() -> Point.builder().user(user).balance(0L).build());

        point.addBalance(request.amount());
        pointRepository.save(point);

        pointHistoryRepository.save(PointHistory.builder()
                .user(user)
                .amount(request.amount())
                .type(PointStatus.CHARGE)
                .build());

        return PointResponse.from(point);
    }

    // 포인트 사용
    @Transactional
    public void usePoint(Long userId, Long amount) {

        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트가 없습니다."));

        point.useBalance(amount);
        pointRepository.save(point);

        pointHistoryRepository.save(PointHistory.builder()
                .user(point.getUser())
                .amount(amount)
                .type(PointStatus.USE)
                .build());
    }
}
