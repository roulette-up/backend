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
     * 사용자에 의한 환불 처리
     */
    fun refundByUser(amount: Long) {
        this.remainingPoint += amount

        // 사용자 환불은 포인트 만료 기한이 지나면 상태 변화 x
        if (this.status != PointStatus.EXPIRED) {
            this.status = PointStatus.AVAILABLE
        }
    }

    /**
     * 어드민에 의한 환불 처리
     * - 사용자 자발적 취소가 아니기에 유효기간 연장
     */
    fun refundByAdmin(amount: Long) {
        this.remainingPoint += amount

        this.expiresAt =
            if (this.status == PointStatus.EXPIRED) {
                // 이미 만료된 경우, 오늘 기준 +3일
                LocalDate.now().plusDays(3)
            } else {
                // 아직 유효한 경우, 기존 만료일 +3일
                this.expiresAt.plusDays(3)
            }

        this.status = PointStatus.AVAILABLE
    }
}
