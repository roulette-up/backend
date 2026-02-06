package kr.co.rouletteup.domain.notification.repository

import kr.co.rouletteup.domain.notification.entity.Notification

interface CustomNotificationRepository {
    fun saveAllInBatch(notifications: List<Notification>)
}
