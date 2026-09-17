package org.example.springtestci.app;

import org.example.springtestci.domain.Flower;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("FlowerService 통합 테스트")
class FlowerServiceIntegrationTest {

    @Autowired
    private FlowerUseCase flowerService;

    @Test
    @DisplayName("꽃을 저장하지 않으면 count는 0이고 findAll은 빈 목록이다")
    void initialState_isEmpty() {
        // when: 아무 것도 저장하지 않은 초기 상태에서 조회
        long count = flowerService.count();
        List<Flower> flowers = flowerService.findAll();

        // then: 개수는 0이고 목록은 비어 있어야 한다
        assertThat(count).isZero();
        assertThat(flowers).isEmpty();
    }

    @Test
    @DisplayName("꽃을 저장하면 실제 저장소에 반영되어 count와 findAll에 나타난다")
    void save_persistsFlowerThroughRealRepository() {
        // given: 저장할 꽃 준비
        Flower rose = new Flower("장미", "빨강", 5000);

        // when: 서비스를 통해 실제 저장소(JPA/H2)에 저장
        Flower savedFlower = flowerService.save(rose);

        // then: 저장 결과와 전체 조회 결과에 실제로 반영되어야 한다
        assertThat(savedFlower).isEqualTo(rose);
        assertThat(flowerService.count()).isEqualTo(1L);
        assertThat(flowerService.findAll()).containsExactly(rose);
    }

    @Test
    @DisplayName("여러 꽃을 저장하면 저장한 순서와 개수가 그대로 조회된다")
    void save_multipleFlowers_reflectsAllInFindAllAndCount() {
        // given: 저장할 꽃 두 개 준비
        Flower rose = new Flower("장미", "빨강", 5000);
        Flower tulip = new Flower("튤립", "노랑", 3000);

        // when: 두 꽃을 순서대로 저장
        flowerService.save(rose);
        flowerService.save(tulip);

        // then: 저장한 개수와 목록이 실제 저장소를 통해 검증되어야 한다
        assertThat(flowerService.count()).isEqualTo(2L);
        assertThat(flowerService.findAll()).containsExactly(rose, tulip);
    }
}
