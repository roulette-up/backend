package kr.co.rouletteup.domain.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import kr.co.rouletteup.domain.notification.type.ExpiryNoticeType

@Entity
@Table(name = "notification")
class Notification(
    expiringPoint: Long,
    isRead: Boolean = false,
    type: ExpiryNoticeType,
    createdAt: LocalDateTime = LocalDateTime.now(),
    expiresAt: LocalDate,
    userId: Long,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "expiring_point", nullable = false)
    var expiringPoint: Long = expiringPoint
        protected set

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = isRead
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: ExpiryNoticeType = type
        protected set

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = createdAt
        protected set

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDate = expiresAt
        protected set

    @Column(name = "user_id", nullable = false)
    var userId: Long = userId
        protected set
}
