package kr.co.rouletteup.domain.notification.repository

import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Timestamp
import kr.co.rouletteup.domain.notification.entity.Notification
import org.springframework.jdbc.core.JdbcTemplate

class CustomNotificationRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
) : CustomNotificationRepository {

    companion object {
        private const val BATCH_SIZE = 100
    }

    /**
     * JDBC를 통한 Batch Insert 처리
     */
    override fun saveAllInBatch(notifications: List<Notification>) {
        val sql = """
           INSERT INTO notification (
                type,
                expiring_point,
                is_read,
                created_at,
                expires_at,
                user_id
            ) VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(
            sql,
            notifications,
            BATCH_SIZE
        ) { ps: PreparedStatement, notification: Notification ->

            ps.setString(1, notification.type.name)
            ps.setLong(2, notification.expiringPoint)
            ps.setBoolean(3, notification.isRead)
            ps.setTimestamp(4, Timestamp.valueOf(notification.createdAt))
            ps.setDate(5, Date.valueOf(notification.expiresAt))
            ps.setLong(6, notification.userId)
        }
    }
}
