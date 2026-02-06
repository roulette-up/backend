package kr.co.rouletteup.app.notification.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.exception.NotificationErrorType
import kr.co.rouletteup.domain.notification.exception.NotificationException
import kr.co.rouletteup.domain.notification.service.NotificationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MarkNotificationAsReadUseCaseTest {

    @MockK
    private lateinit var notificationService: NotificationService

    @InjectMockKs
    private lateinit var markNotificationAsReadUseCase: MarkNotificationAsReadUseCase

    @Test
    fun `알림이 존재하면 markAsRead를 호출한다`() {
        // given
        val notificationId = 10L
        val notification = mockk<Notification>(relaxed = true)

        every { notificationService.readById(notificationId) } returns notification

        // when
        markNotificationAsReadUseCase.markNotificationAsRead(notificationId)

        // then
        verify(exactly = 1) { notificationService.readById(notificationId) }
        verify(exactly = 1) { notification.markAsRead() }
    }

    @Test
    fun `알림이 존재하지 않으면 NOT_FOUND 예외를 던진다`() {
        // given
        val notificationId = 999L
        every { notificationService.readById(notificationId) } returns null

        // when
        val exception = assertThrows(NotificationException::class.java) {
            markNotificationAsReadUseCase.markNotificationAsRead(notificationId)
        }

        // then
        assertEquals(NotificationErrorType.NOT_FOUND, exception.errorType)
        verify(exactly = 1) { notificationService.readById(notificationId) }
    }
}
