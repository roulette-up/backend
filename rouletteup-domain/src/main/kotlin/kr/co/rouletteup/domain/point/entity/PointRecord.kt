package kr.co.rouletteup.domain.point.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.user.entity.User
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE roulette_point_record SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "point_record")
class PointRecord(
    grantedPoint: Long,
    remainingPoint: Long,
    status: PointStatus,
    expiresAt: LocalDate,
    user: User,
    dailyRoulette: DailyRoulette,
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roulette_id", nullable = false)
    var dailyRoulette: DailyRoulette = dailyRoulette
        protected set
}
