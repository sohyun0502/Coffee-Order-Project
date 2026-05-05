package kr.spartaclub.coffeeorderproject.domain.point.controller;

import kr.spartaclub.coffeeorderproject.common.response.ApiResponse;
import kr.spartaclub.coffeeorderproject.domain.menu.dto.MenuResponse;
import kr.spartaclub.coffeeorderproject.domain.point.dto.PointRequest;
import kr.spartaclub.coffeeorderproject.domain.point.dto.PointResponse;
import kr.spartaclub.coffeeorderproject.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;

    /**
     * 2. 포인트 충전 하기 API
     * @param request
     * @return
     */
    @PatchMapping("/charge")
    public ResponseEntity<ApiResponse<PointResponse>> chargePoint (@RequestBody PointRequest request) {
        return ResponseEntity.ok(ApiResponse.success(pointService.chargePoint(request)));
    }
}
