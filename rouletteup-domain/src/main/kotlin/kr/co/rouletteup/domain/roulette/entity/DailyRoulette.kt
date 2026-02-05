package kr.co.rouletteup.domain.roulette.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate
import kr.co.rouletteup.domain.common.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE daily_roulette SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "daily_roulette")
class DailyRoulette(
    rouletteDate: LocalDate,
    totalBudget: Long,
    remainingBudget: Long,
    participantCount: Int,
) : BaseEntity() {

    // 룰렛 날짜
    @Column(name = "roulette_date", nullable = false)
    var rouletteDate: LocalDate = rouletteDate
        protected set

    // 일일 예산
    @Column(name = "total_budget", nullable = false)
    var totalBudget: Long = totalBudget
        protected set

    // 남은 예산
    @Column(name = "remaining_budget", nullable = false)
    var remainingBudget: Long = remainingBudget
        protected set

    // 참여자 수
    @Column(name = "participant_count", nullable = false)
    var participantCount: Int = participantCount
        protected set
}
