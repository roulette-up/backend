package kr.co.rouletteup.domain.notification.service

import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.repository.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun saveAll(notifications: List<Notification>) {
        notificationRepository.saveAllInBatch(notifications)
    }
}
