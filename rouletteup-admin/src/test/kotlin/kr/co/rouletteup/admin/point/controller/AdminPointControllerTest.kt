package kr.co.rouletteup.admin.point.controller

import java.time.LocalDate
import kr.co.rouletteup.admin.point.dto.AdminPointRes
import kr.co.rouletteup.admin.point.usecase.GetPointForAdminUseCase
import kr.co.rouletteup.domain.point.type.PointStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminPointController::class)
class AdminPointControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getPointForAdminUseCase: GetPointForAdminUseCase

    @Nested
    @DisplayName("유저 포인트 내역 조회 API")
    inner class GetPointRecordsByUserId {

        @Test
        fun `userId로 포인트 내역을 페이징 조회한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 20)

            val response = AdminPointRes(
                id = 10L,
                userId = userId,
                grantedPoint = 500L,
                remainingPoint = 200L,
                status = PointStatus.AVAILABLE,
                expiresAt = LocalDate.of(2026, 3, 3),
                rouletteDate = LocalDate.of(2026, 2, 1),
                deletedAt = null
            )
            val page = PageImpl(listOf(response), pageable, 1)

            given(getPointForAdminUseCase.getPointRecordByUserId(userId, pageable))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/points/users/$userId") {
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
                    jsonPath("$.data.content[0].userId").value(response.userId)
                    jsonPath("$.data.content[0].grantedPoint").value(response.grantedPoint)
                }
        }
    }

    @Nested
    @DisplayName("룰렛 날짜별 포인트 내역 조회 API")
    inner class GetPointRecordsByRouletteDate {

        @Test
        fun `rouletteDate로 포인트 내역을 페이징 조회한다`() {
            // given
            val rouletteDate = LocalDate.of(2026, 2, 7)
            val pageable = PageRequest.of(0, 20)

            val response = AdminPointRes(
                id = 10L,
                userId = 1L,
                grantedPoint = 500L,
                remainingPoint = 200L,
                status = PointStatus.AVAILABLE,
                expiresAt = LocalDate.of(2026, 3, 3),
                rouletteDate = LocalDate.of(2026, 2, 1),
                deletedAt = null
            )
            val page = PageImpl(listOf(response), pageable, 1)

            given(getPointForAdminUseCase.getPointRecordByRouletteDate(rouletteDate, pageable))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/points/roulettes/$rouletteDate") {
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
                    jsonPath("$.data.content[0].status").value(response.status)
                }
        }
    }

}
