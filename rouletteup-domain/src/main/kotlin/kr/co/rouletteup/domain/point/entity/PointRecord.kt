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
}
