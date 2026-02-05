package kr.co.rouletteup.app.roulette.controller

import kr.co.rouletteup.app.roulette.dto.RouletteRes
import kr.co.rouletteup.app.roulette.usercase.GetRouletteUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(RouletteController::class)
class RouletteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getRouletteUseCase: GetRouletteUseCase

    @Nested
    @DisplayName("금일 남은 예산 조회 API")
    inner class GetTodayRemainingBudget {
        @Test
        fun `오늘 룰렛 잔여 예산 조회`() {
            // given
            val response = RouletteRes(10000L)

            given(getRouletteUseCase.getTodayRemainingBudget())
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/roulettes/today") {
                contentType = MediaType.APPLICATION_JSON
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.remainingBudget").value(response.remainingBudget)
                }
        }
    }
}
