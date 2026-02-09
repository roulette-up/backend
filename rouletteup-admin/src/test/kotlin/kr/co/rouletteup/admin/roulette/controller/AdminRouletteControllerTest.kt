package kr.co.rouletteup.admin.roulette.controller

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalDateTime
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetReq
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetRes
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteRes
import kr.co.rouletteup.admin.roulette.usecase.GetRouletteForAdminUseCase
import kr.co.rouletteup.admin.roulette.usecase.UpdateRouletteBudgetForAdminUseCase
import kr.co.rouletteup.common.auth.AdminAuthInterceptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminRouletteController::class)
class AdminRouletteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var getRouletteForAdminUseCase: GetRouletteForAdminUseCase

    @MockitoBean
    private lateinit var updateRouletteBudgetForAdminUseCase: UpdateRouletteBudgetForAdminUseCase

    @MockitoBean
    private lateinit var adminAuthInterceptor: AdminAuthInterceptor

    @BeforeEach
    fun setUp() {
        given(adminAuthInterceptor.preHandle(any(), any(), any())).willReturn(true)
    }

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

    @Nested
    @DisplayName("미래 예산 설정 조회 API")
    inner class GetFutureSettingsBudget {

        @Test
        fun `성공 - 미래 예산 설정을 조회하면 200을 반환한다`() {
            // given
            val response = listOf(
                AdminRouletteBudgetRes(
                    id = 1L,
                    settingDate = LocalDate.of(2026, 2, 9),
                    totalBudget = 120_000L,
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                ),
                AdminRouletteBudgetRes(
                    id = 2L,
                    settingDate = LocalDate.of(2026, 2, 10),
                    totalBudget = 100_000L,
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
            )

            given(getRouletteForAdminUseCase.getFutureSettingsBudget())
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/roulettes/future/budget")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data").isArray
                    jsonPath("$.data.length()").value(2)
                    jsonPath("$.data[0].totalBudget").value(120_000)
                }
        }
    }

    @Nested
    @DisplayName("금일 예산 수정 API")
    inner class UpdateTodayBudget {

        @Test
        fun `성공 - 금일 예산 수정 요청이면 200을 반환한다`() {
            // given
            val request = AdminRouletteBudgetReq.UpdateToday(
                newTotalBudget = 150_000L
            )

            willDoNothing().given(updateRouletteBudgetForAdminUseCase).updateTodayBudget(any())

            // when
            val resultActions = mockMvc.patch("/api/v1/admin/roulettes/today/budget") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                }
        }
    }

    @Nested
    @DisplayName("미래 날짜 예산 수정(Upsert) API")
    inner class UpsertFutureBudget {

        @Test
        fun `성공 - 미래 날짜 예산 수정 요청이면 200을 반환한다`() {
            // given
            val request = AdminRouletteBudgetReq.UpdateFuture(
                targetDate = LocalDate.now().plusDays(2),
                newTotalBudget = 110_000L
            )

            willDoNothing().given(updateRouletteBudgetForAdminUseCase).upsertFutureBudget(any())

            // when
            val resultActions = mockMvc.patch("/api/v1/admin/roulettes/future/budget") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                }
        }
    }

}
