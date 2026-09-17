package org.example.springtestci.ui;

import org.example.springtestci.app.FlowerUseCase;
import org.example.springtestci.domain.Flower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("FlowerApiController 단위 테스트")
class FlowerApiControllerTest {

    private final FlowerUseCase flowerUseCase = mock(FlowerUseCase.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 목(mock) 유스케이스를 주입한 컨트롤러로 MockMvc 구성
        mockMvc = MockMvcBuilders.standaloneSetup(new FlowerApiController(flowerUseCase)).build();
    }

    @Test
    @DisplayName("GET /api/flowers/count 호출 시 유스케이스의 개수를 응답한다")
    void getCount_returnsCountFromUseCase() throws Exception {
        // given: 유스케이스가 개수 3을 반환하도록 설정
        given(flowerUseCase.count()).willReturn(3L);

        // when & then: count 조회 API가 유스케이스 결과를 응답해야 한다
        mockMvc.perform(get("/api/flowers/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("POST /api/flowers 호출 시 꽃을 저장하고 저장된 결과를 응답한다")
    void postFlower_savesAndReturnsSavedFlower() throws Exception {
        // given: 요청 JSON과 유스케이스의 저장 결과 설정
        String requestJson = """
                {"name":"장미","color":"빨강","price":5000}
                """;
        Flower savedFlower = new Flower("장미", "빨강", 5000);
        given(flowerUseCase.save(new Flower("장미", "빨강", 5000))).willReturn(savedFlower);

        // when & then: 저장 API가 저장된 꽃 정보를 응답해야 한다
        mockMvc.perform(post("/api/flowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("장미"))
                .andExpect(jsonPath("$.color").value("빨강"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @Test
    @DisplayName("GET /api/flowers 호출 시 유스케이스의 전체 목록을 응답한다")
    void getFlowers_returnsAllFlowersFromUseCase() throws Exception {
        // given: 유스케이스가 꽃 목록을 반환하도록 설정
        Flower rose = new Flower("장미", "빨강", 5000);
        Flower tulip = new Flower("튤립", "노랑", 3000);
        given(flowerUseCase.findAll()).willReturn(List.of(rose, tulip));

        // when & then: 목록 조회 API가 전체 꽃 목록을 응답해야 한다
        mockMvc.perform(get("/api/flowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("장미"))
                .andExpect(jsonPath("$[1].name").value("튤립"));
    }
}
