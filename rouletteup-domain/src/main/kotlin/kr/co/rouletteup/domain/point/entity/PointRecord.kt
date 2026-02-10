package kr.co.rouletteup.domain.point.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDate
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.point.type.PointStatus
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE point_record SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "point_record")
class PointRecord(
    grantedPoint: Long,
    remainingPoint: Long,
    status: PointStatus,
    expiresAt: LocalDate,
    userId: Long,
    rouletteDate: LocalDate,
) : BaseEntity() {

    // 발급 받은 포인트
    @Column(name = "granted_point", nullable = false)
    var grantedPoint: Long = grantedPoint
        protected set

    // 사용 후 남은 포인트
    @Column(name = "remaining_point", nullable = false)
    var remainingPoint: Long = remainingPoint
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PointStatus = status
        protected set

    // 만료 일자
    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDate = expiresAt
        protected set

    @Column(name = "user_id", nullable = false)
    var userId: Long = userId
        protected set

    @Column(name = "roulette_date", nullable = false)
    var rouletteDate: LocalDate = rouletteDate
        protected set

    fun use(amount: Long) {
        remainingPoint -= amount
        if (remainingPoint == 0L) {
            status = PointStatus.USED
        }
    }

    /**
     * 만료기한 지났는지 확인
     */
    private fun isExpiredToday(today: LocalDate = LocalDate.now()): Boolean =
        expiresAt.isBefore(today)

    /**
     * 사용자에 의한 환불 처리
     *
     * - CANCELED: 무시
     * - USED:
     *   - 만료 안 됨 -> AVAILABLE
     *   - 만료 됨 -> EXPIRED
     * - AVAILABLE: 그대로 AVAILABLE
     * - EXPIRED: 그대로 EXPIRED
     */
    fun refundByUser(amount: Long, today: LocalDate = LocalDate.now()) {
        if (status == PointStatus.CANCELED) return

        remainingPoint += amount

        status = when (status) {
            PointStatus.USED ->
                if (isExpiredToday(today)) PointStatus.EXPIRED else PointStatus.AVAILABLE

            PointStatus.AVAILABLE -> PointStatus.AVAILABLE
            PointStatus.EXPIRED -> PointStatus.EXPIRED
            PointStatus.CANCELED -> PointStatus.CANCELED
        }
    }

    /**
     * 어드민에 의한 환불 처리 및 부채 삭감 포인트 환급
     *
     * - CANCELED: 무시
     * - USED:
     *   - 만료 안 됨 -> expiresAt + 3일, AVAILABLE
     *   - 만료 됨 -> today + 3일, AVAILABLE
     * - AVAILABLE: expiresAt + 3일, AVAILABLE
     * - EXPIRED: today + 3일, AVAILABLE
     */
    fun restore(amount: Long, today: LocalDate = LocalDate.now()) {
        if (status == PointStatus.CANCELED) return

        remainingPoint += amount

        expiresAt = when (status) {
            PointStatus.USED ->
                if (isExpiredToday(today)) today.plusDays(3) else expiresAt.plusDays(3)

            PointStatus.AVAILABLE -> expiresAt.plusDays(3)
            PointStatus.EXPIRED -> today.plusDays(3)
            PointStatus.CANCELED -> expiresAt
        }

        status = PointStatus.AVAILABLE
    }

    /**
     * 포인트 회수 처리 편의 메서드
     */
    fun markAsCanceled() {
        this.remainingPoint = 0L
        this.status = PointStatus.CANCELED
    }
}
