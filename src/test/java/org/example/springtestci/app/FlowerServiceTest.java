package org.example.springtestci.app;

import org.example.springtestci.domain.Flower;
import org.example.springtestci.domain.FlowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("FlowerService 단위 테스트")
class FlowerServiceTest {

    private final FlowerRepository flowerRepository = mock(FlowerRepository.class);

    private FlowerService flowerService;

    @BeforeEach
    void setUp() {
        // 목(mock) 저장소를 주입해 서비스 생성
        flowerService = new FlowerService(flowerRepository);
    }

    @Test
    @DisplayName("count 호출 시 저장소의 count 결과를 그대로 반환한다")
    void count_delegatesToRepository() {
        // given: 저장소가 개수 3을 반환하도록 설정
        given(flowerRepository.count()).willReturn(3L);

        // when: 서비스의 count 메서드를 호출
        long count = flowerService.count();

        // then: 저장소가 반환한 값과 일치해야 한다
        assertThat(count).isEqualTo(3L);
    }

    @Test
    @DisplayName("save 호출 시 저장소에 위임하고 저장된 꽃을 반환한다")
    void save_delegatesToRepository() {
        // given: 저장할 꽃과 저장소의 반환값 설정
        Flower rose = new Flower("장미", "빨강", 5000);
        given(flowerRepository.save(rose)).willReturn(rose);

        // when: 서비스의 save 메서드를 호출
        Flower savedFlower = flowerService.save(rose);

        // then: 저장소 호출 결과와 위임 여부를 검증
        assertThat(savedFlower).isEqualTo(rose);
        verify(flowerRepository, times(1)).save(rose);
    }

    @Test
    @DisplayName("findAll 호출 시 저장소의 목록을 그대로 반환한다")
    void findAll_delegatesToRepository() {
        // given: 저장소가 꽃 목록을 반환하도록 설정
        Flower rose = new Flower("장미", "빨강", 5000);
        Flower tulip = new Flower("튤립", "노랑", 3000);
        given(flowerRepository.findAll()).willReturn(List.of(rose, tulip));

        // when: 서비스의 findAll 메서드를 호출
        List<Flower> flowers = flowerService.findAll();

        // then: 저장소가 반환한 목록과 일치해야 한다
        assertThat(flowers).containsExactly(rose, tulip);
    }
}
