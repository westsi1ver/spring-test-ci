package org.example.springtestci.ui;

import lombok.RequiredArgsConstructor;
import org.example.springtestci.app.FlowerUseCase;
import org.example.springtestci.domain.Flower;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flowers")
@RequiredArgsConstructor
public class FlowerApiController {
    private final FlowerUseCase flowerUseCase;

    @GetMapping("/count")
    public long count() {
        // 유스케이스에 개수 조회를 위임
        return flowerUseCase.count();
    }

    @PostMapping
    public FlowerDto save(@RequestBody FlowerDto request) {
        // 요청 DTO를 도메인 객체로 변환 후 저장을 위임
        Flower saved = flowerUseCase.save(new Flower(request.name(), request.color(), request.price()));
        return toDto(saved);
    }

    @GetMapping
    public List<FlowerDto> findAll() {
        // 유스케이스의 전체 목록을 응답 DTO 목록으로 변환
        return flowerUseCase.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private FlowerDto toDto(Flower flower) {
        // 도메인 객체를 응답 DTO로 매핑
        return new FlowerDto(flower.name(), flower.color(), flower.price());
    }
}
