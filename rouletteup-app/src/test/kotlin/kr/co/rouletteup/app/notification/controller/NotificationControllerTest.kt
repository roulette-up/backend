package kr.co.rouletteup.app.notification.controller

import kotlin.test.Test
import kr.co.rouletteup.app.auth.controller.AuthController
import kr.co.rouletteup.app.notification.dto.NotificationRes
import kr.co.rouletteup.app.notification.usecase.GetNotificationUseCase
import kr.co.rouletteup.app.notification.usecase.MarkNotificationAsReadUseCase
import kr.co.rouletteup.internal.filter.AdminInternalAuthFilter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch

@WebMvcTest(
    controllers = [NotificationController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [AdminInternalAuthFilter::class]
        )
    ]
)
class NotificationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getNotificationUseCase: GetNotificationUseCase

    @MockitoBean
    private lateinit var markNotificationAsReadUseCase: MarkNotificationAsReadUseCase

    @Nested
    @DisplayName("알림 리스트 조회 API")
    inner class GetNotifications {

        @Test
        fun `cursorId 없이 조회하면 200을 반환한다`() {
            // given
            val userId = 1L
            val limit = 20
            val response = NotificationRes(
                emptyList<NotificationRes.Item>(),
                true,
                90L
            )

            given(getNotificationUseCase.getNotificationsSliceByCursor(userId, null, limit))
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/notifications") {
                header("X-User-Id", userId)
                param("limit", limit.toString())
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code") { value(200) }
                    jsonPath("$.message") { value("요청이 성공하였습니다.") }
                }

        }

        @Test
        fun `성공 - cursorId 포함 조회하면 200을 반환한다`() {
            // given
            val userId = 1L
            val cursorId = 100L
            val limit = 30
            val response = NotificationRes(
                emptyList<NotificationRes.Item>(),
                true,
                90L
            )

            given(getNotificationUseCase.getNotificationsSliceByCursor(userId, cursorId, limit))
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/notifications") {
                header("X-User-Id", userId)
                param("cursorId", cursorId.toString())
                param("limit", limit.toString())
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code") { value(200) }
                    jsonPath("$.message") { value("요청이 성공하였습니다.") }
                }
        }
    }

    @Nested
    @DisplayName("알림 읽음 처리 API")
    inner class MarkAsRead {

        @Test
        fun `읽음 처리하면 200을 반환한다`() {
            // given
            val notificationId = 10L
            willDoNothing()
                .given(markNotificationAsReadUseCase)
                .markNotificationAsRead(notificationId)

            // when
            val resultActions = mockMvc.patch("/api/v1/notifications/$notificationId/read-confirm")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code") { value(200) }
                    jsonPath("$.message") { value("요청이 성공하였습니다.") }
                }
        }
    }
}
