package kr.co.rouletteup.admin.roulette.controller

import java.time.LocalDate
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteRes
import kr.co.rouletteup.admin.roulette.usecase.GetRouletteForAdminUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminRouletteController::class)
class AdminRouletteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getRouletteForAdminUseCase: GetRouletteForAdminUseCase

    @Nested
    @DisplayName("룰렛 목록 조회 API")
    inner class GetRoulettes {

        @Test
        fun `페이징으로 soft delete 포함 룰렛 목록 조회`() {
            // given
            val pageable = PageRequest.of(0, 20)

            val response = AdminRouletteRes(
                id = 1L,
                rouletteDate = LocalDate.of(2026, 2, 7),
                totalBudget = 100_000L,
                usedBudget = 30_000L,
                participantCount = 50,
                deletedAt = null
            )

            val page = PageImpl(listOf(response), pageable, 1)

            given(getRouletteForAdminUseCase.getRoulettes(pageable))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/roulettes") {
                param("page", "0")
                param("size", "20")
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.content[0].id").value(response.id)
                    jsonPath("$.data.content[0].rouletteDate").value(response.rouletteDate)
                    jsonPath("$.data.content[0].totalBudget").value(response.totalBudget)
                    jsonPath("$.data.content[0].usedBudget").value(response.usedBudget)
                }
        }
    }

    @Nested
    @DisplayName("오늘 룰렛 조회 API")
    inner class GetTodayRoulette {

        @Test
        fun `오늘 룰렛 정보를 조회한다`() {
            // given
            val response = AdminRouletteRes(
                id = 1L,
                rouletteDate = LocalDate.of(2026, 2, 7),
                totalBudget = 100_000L,
                usedBudget = 30_000L,
                participantCount = 50,
                deletedAt = null
            )

            given(getRouletteForAdminUseCase.getTodayRoulette())
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/roulettes/today")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.id").value(response.id)
                    jsonPath("$.data.rouletteDate").value(response.rouletteDate)
                    jsonPath("$.data.totalBudget").value(response.totalBudget)
                    jsonPath("$.data.usedBudget").value(response.usedBudget)
                    jsonPath("$.data.participantCount").value(response.participantCount)
                }
        }
    }
}
