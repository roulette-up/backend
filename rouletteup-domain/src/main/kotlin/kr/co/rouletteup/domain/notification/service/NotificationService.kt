package kr.co.rouletteup.domain.notification.service

import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.repository.NotificationRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {

    fun saveAll(notifications: List<Notification>) {
        notificationRepository.saveAllInBatch(notifications)
    }

    fun readById(id: Long): Notification? =
        notificationRepository.findByIdOrNull(id)

    fun readFirstPage(userId: Long, pageable: Pageable): List<Notification> =
        notificationRepository.findFirstPage(userId, pageable)

    fun readNextPage(userId: Long, cursorId: Long, pageable: Pageable): List<Notification> =
        notificationRepository.findNextPage(userId, cursorId, pageable)

}
