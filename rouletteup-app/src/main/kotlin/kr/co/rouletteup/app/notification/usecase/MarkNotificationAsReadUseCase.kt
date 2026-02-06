package kr.co.rouletteup.app.notification.usecase

import kr.co.rouletteup.domain.notification.exception.NotificationErrorType
import kr.co.rouletteup.domain.notification.exception.NotificationException
import kr.co.rouletteup.domain.notification.service.NotificationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarkNotificationAsReadUseCase(
    private val notificationService: NotificationService,
) {

    /**
     * 알림 읽을 처리 메서드
     *
     * @param notificationId 알림 ID(PK)
     */
    @Transactional
    fun markNotificationAsRead(notificationId: Long) {
        val notification = notificationService.readById(notificationId)
            ?: throw NotificationException(NotificationErrorType.NOT_FOUND)

        notification.markAsRead()
    }
}
