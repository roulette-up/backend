package kr.co.rouletteup.app.point.controller

import kotlin.test.Test
import kr.co.rouletteup.app.auth.controller.AuthController
import kr.co.rouletteup.app.point.usecase.GetPointRecordUseCase
import kr.co.rouletteup.internal.filter.AdminInternalAuthFilter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(
    controllers = [PointController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [AdminInternalAuthFilter::class]
        )
    ]
)
class PointControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var getPointRecordUseCase: GetPointRecordUseCase

    @Nested
    @DisplayName("포인트 내역 조회 API")
    inner class GetMyRecords {

        @Test
        fun `내 포인트 기록을 페이징으로 조회한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 10)

            given(getPointRecordUseCase.getMyRecords(eq(userId), any()))
                .willReturn(PageImpl(emptyList(), pageable, 0))

            // when
            val resultActions = mockMvc.get("/api/v1/points/records") {
                header("X-User-Id", userId)
                accept = MediaType.APPLICATION_JSON
            }

            // then
            resultActions.andExpect {
                status { isOk() }
                jsonPath("$.code") { value(200) }
                jsonPath("$.message") { value("요청이 성공하였습니다.") }
                jsonPath("$.data") { exists() }
            }
        }
    }
}
