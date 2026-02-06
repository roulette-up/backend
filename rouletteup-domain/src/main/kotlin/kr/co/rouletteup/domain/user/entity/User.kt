package kr.co.rouletteup.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.user.type.Role
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "users")
class User(
    nickname: String,
    role: Role,
    pointDebt: Long = 0,
) : BaseEntity() {

    @Column(length = 30, nullable = false, unique = true)
    var nickname: String = nickname
        protected set

    // 포인트 부채 - 특정 포인트를 사용했지만, 어드민이 회수하는 경우
    @Column(name = "point_debt", nullable = false)
    var pointDebt: Long = pointDebt
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = role
        protected set

    /**
     * 포인트 부채 상환 편의 메서드
     */
    fun repayDebt(point: Long): Long {
        if (pointDebt <= 0L) return 0L

        val repaid = minOf(pointDebt, point)
        pointDebt -= repaid
        return repaid
    }
}
