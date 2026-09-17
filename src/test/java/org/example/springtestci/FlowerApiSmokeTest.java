package org.example.springtestci;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 컨트롤러부터 실제 JPA/H2까지 전체 스택을 목(mock) 없이 검증
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Flower 기능 스모크 테스트")
class FlowerApiSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("꽃을 저장하면 실제 서버를 통해 count와 목록 조회에 즉시 반영된다")
    void flowerLifecycle_worksEndToEndThroughRealStack() throws Exception {
        // given: 초기 상태는 저장된 꽃이 없어야 한다
        mockMvc.perform(get("/api/flowers/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        // when: 실제 HTTP 요청으로 꽃을 저장
        String requestJson = """
                {"name":"장미","color":"빨강","price":5000}
                """;
        mockMvc.perform(post("/api/flowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("장미"))
                .andExpect(jsonPath("$.color").value("빨강"))
                .andExpect(jsonPath("$.price").value(5000));

        // then: 실제 저장소를 거쳐 count와 목록 조회에 반영되어야 한다
        mockMvc.perform(get("/api/flowers/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(get("/api/flowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("장미"))
                .andExpect(jsonPath("$[0].color").value("빨강"))
                .andExpect(jsonPath("$[0].price").value(5000));
    }
}
