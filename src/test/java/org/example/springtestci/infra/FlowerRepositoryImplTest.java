package org.example.springtestci.infra;

import org.example.springtestci.domain.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // h2
@DisplayName("FlowerRepositoryImpl 단위 테스트")
class FlowerRepositoryImplTest {

    @Autowired
    private FlowerJpaRepository flowerJpaRepository;

    private FlowerRepositoryImpl flowerRepository;

    @BeforeEach // 테스트마다 작동
    void setUp() {
        // 실제 JPA 저장소를 주입해 구현체 생성
        flowerRepository = new FlowerRepositoryImpl(flowerJpaRepository);
    }

    @Test
    @DisplayName("저장된 꽃이 없으면 count는 0을 반환한다")
    void count_returnsZero_whenNoFlowerSaved() {
        // when: 아무것도 저장하지 않은 상태에서 count 호출
        long count = flowerRepository.count();

        // then: 개수는 0이어야 한다
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("꽃을 저장하면 count가 1 증가한다")
    void count_increasesByOne_afterSavingFlower() {
        // given: 저장할 꽃 객체 준비
        Flower rose = new Flower("장미", "빨강", 5000);

        // when: 꽃을 저장
        flowerRepository.save(rose);

        // then: count는 1이어야 한다
        assertThat(flowerRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("save 호출 시 저장한 꽃 정보를 그대로 반환한다")
    void save_returnsSavedFlower() {
        // given: 저장할 꽃 객체 준비
        Flower rose = new Flower("장미", "빨강", 5000);

        // when: 꽃을 저장
        Flower savedFlower = flowerRepository.save(rose);

        // then: 반환된 객체가 원본과 동일해야 한다
        assertThat(savedFlower).isEqualTo(rose);
    }

    @Test
    @DisplayName("저장된 꽃이 없으면 findAll은 빈 목록을 반환한다")
    void findAll_returnsEmptyList_whenNoFlowerSaved() {
        // when: 아무것도 저장하지 않은 상태에서 findAll 호출
        List<Flower> flowers = flowerRepository.findAll();

        // then: 빈 목록이 반환되어야 한다
        assertThat(flowers).isEmpty();
    }

    @Test
    @DisplayName("꽃을 저장하면 findAll에 해당 꽃이 포함된다")
    void findAll_containsSavedFlower_afterSaving() {
        // given: 저장할 꽃 두 개 준비
        Flower rose = new Flower("장미", "빨강", 5000);
        Flower tulip = new Flower("튤립", "노랑", 3000);

        // when: 두 꽃을 순서대로 저장
        flowerRepository.save(rose);
        flowerRepository.save(tulip);

        // then: 저장한 꽃들이 목록에 모두 포함되어야 한다
        assertThat(flowerRepository.findAll()).containsExactly(rose, tulip);
    }
}
