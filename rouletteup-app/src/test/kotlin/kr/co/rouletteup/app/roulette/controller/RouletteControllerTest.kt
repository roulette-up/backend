package kr.co.rouletteup.app.roulette.controller

import kr.co.rouletteup.app.roulette.dto.RouletteParticipateRes
import kr.co.rouletteup.app.roulette.dto.RouletteRes
import kr.co.rouletteup.app.roulette.dto.RouletteStatusRes
import kr.co.rouletteup.app.roulette.usecase.CheckRouletteParticipationUseCase
import kr.co.rouletteup.app.roulette.usecase.GetRouletteUseCase
import kr.co.rouletteup.app.roulette.usecase.ParticipateRouletteUseCase
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(RouletteController::class)
class RouletteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getRouletteUseCase: GetRouletteUseCase

    @MockitoBean
    private lateinit var checkRouletteParticipationUseCase: CheckRouletteParticipationUseCase

    @MockitoBean
    private lateinit var participateRouletteUseCase: ParticipateRouletteUseCase

    @Nested
    @DisplayName("룰렛 참여 API")
    inner class Participate {

        @Test
        fun `X-User-Id 헤더로 참여 요청`() {
            // given
            val userId = 1L
            val participateRes = RouletteParticipateRes(300L)
            given(participateRouletteUseCase.participate(userId)).willReturn(participateRes)

            // when
            val resultActions = mockMvc.post("/api/v1/roulettes/today/participation") {
                header("X-User-Id", userId)
                contentType = MediaType.APPLICATION_JSON
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.reward").value(participateRes.reward)
                }
        }
    }

    @Nested
    @DisplayName("금일 남은 예산 조회 API")
    inner class GetTodayBudget {
        @Test
        fun `오늘 룰렛 예산 조회`() {
            // given
            val response = RouletteRes(100_000L, 70_000L)

            given(getRouletteUseCase.getTodayBudget())
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
                    jsonPath("$.data.totalBudget").value(response.totalBudget)
                    jsonPath("$.data.usedBudget").value(response.usedBudget)
                }
        }
    }

    @Nested
    @DisplayName("금일 룰렛 참여 확인 API")
    inner class CheckTodayParticipation {

        @Test
        fun `X-User-Id 헤더로 룰렛 참여 확인 요청`() {
            // given
            val userId = 1L
            val response = RouletteStatusRes(true)

            given(checkRouletteParticipationUseCase.checkTodayParticipation(userId)).willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/roulettes/today/participation") {
                header("X-User-Id", userId)
                accept = MediaType.APPLICATION_JSON
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.participated").value(true)
                }
        }
    }
}
