package kr.co.rouletteup.app.notification.dto

import java.time.LocalDate
import java.time.LocalDateTime
import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.type.ExpiryNoticeType

data class NotificationRes(
    val items: List<Item>,
    val hasNext: Boolean,
    val nextCursor: Long?
) {
    data class Item(
        val id: Long,
        val type: ExpiryNoticeType,
        val expiringPoint: Long,
        val expiresAt: LocalDate,
        val createdAt: LocalDateTime,
        val isRead: Boolean
    )

    companion object {
        fun from(
            notifications: List<Notification>,
            pageSize: Int
        ): NotificationRes {

            val hasNext = notifications.size > pageSize

            // size+1로 조회했을 경우 마지막 1개는 다음 여부 판단용
            val content = if (hasNext) {
                notifications.dropLast(1)
            } else {
                notifications
            }

            val items = content.map { notification ->
                Item(
                    id = notification.id!!,
                    type = notification.type,
                    expiringPoint = notification.expiringPoint,
                    expiresAt = notification.expiresAt,
                    createdAt = notification.createdAt,
                    isRead = notification.isRead
                )
            }

            return NotificationRes(
                items = items,
                hasNext = hasNext,
                nextCursor = content.lastOrNull()?.id
            )
        }
    }
}
