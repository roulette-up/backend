package kr.co.rouletteup.domain.notification.repository

import kr.co.rouletteup.domain.notification.entity.Notification
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NotificationRepository : JpaRepository<Notification, Long>, CustomNotificationRepository {

    @Query(
        value = """
            SELECT *
            FROM notification
            WHERE user_id = :userId
            ORDER BY id DESC
    """, nativeQuery = true
    )
    fun findFirstPage(userId: Long, pageable: Pageable): List<Notification>

    @Query(
        value = """
            SELECT *
            FROM notification
            WHERE user_id = :userId
              AND id < :cursorId
            ORDER BY id DESC
    """, nativeQuery = true
    )
    fun findNextPage(userId: Long, cursorId: Long, pageable: Pageable): List<Notification>
}
