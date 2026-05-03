package kr.spartaclub.coffeeorderproject.domain.point.controller;

import kr.spartaclub.coffeeorderproject.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;
}
