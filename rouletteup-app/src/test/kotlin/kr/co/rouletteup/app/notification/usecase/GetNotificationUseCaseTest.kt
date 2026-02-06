package kr.co.rouletteup.app.notification.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.service.NotificationService
import kr.co.rouletteup.domain.notification.type.ExpiryNoticeType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetNotificationUseCaseTest {

    @MockK
    private lateinit var notificationService: NotificationService

    @InjectMockKs
    private lateinit var getNotificationUseCase: GetNotificationUseCase

    @Test
    fun `cursorId가 null이면 첫 페이지를 조회하고 NotificationRes로 변환한다`() {
        // given
        val userId = 1L
        val cursorId: Long? = null
        val limit = 20

        val notifications = listOf(notificationWithId(5L))

        every { notificationService.readFirstPage(eq(userId), any()) } returns notifications

        // when
        val result =
            getNotificationUseCase.getNotificationsSliceByCursor(userId, cursorId, limit)

        // then
        verify(exactly = 1) { notificationService.readFirstPage(eq(userId), any()) }
        verify(exactly = 0) { notificationService.readNextPage(any(), any(), any()) }

        // 핵심 검증
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].id).isEqualTo(5L)
        assertThat(result.hasNext).isFalse()
        assertThat(result.nextCursor).isEqualTo(5L)
    }

    @Test
    fun `cursorId가 있으면 다음 페이지를 조회하고 NotificationRes로 변환한다`() {
        // given
        val userId = 1L
        val cursorId = 100L
        val limit = 20

        val notifications = listOf(notificationWithId(101L))

        every { notificationService.readNextPage(eq(userId), eq(cursorId), any()) } returns notifications

        // when
        val result =
            getNotificationUseCase.getNotificationsSliceByCursor(userId, cursorId, limit)

        // then
        verify(exactly = 0) { notificationService.readFirstPage(any(), any()) }
        verify(exactly = 1) { notificationService.readNextPage(eq(userId), eq(cursorId), any()) }

        // 핵심 검증
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].id).isEqualTo(101L)
        assertThat(result.hasNext).isFalse()
        assertThat(result.nextCursor).isEqualTo(101L)
    }

    private fun notificationWithId(id: Long): Notification {
        val n = Notification(
            expiringPoint = 10,
            type = ExpiryNoticeType.D3,
            expiresAt = LocalDate.now(),
            userId = 1L,
        )
        val field = Notification::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.set(n, id)
        return n
    }
}
