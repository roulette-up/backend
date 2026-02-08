package kr.co.rouletteup.domain.roulette.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate
import kr.co.rouletteup.domain.common.entity.BaseEntity

@Entity
@Table(name = "roulette_budget_setting")
class RouletteBudgetSetting(
    settingDate: LocalDate,
    totalBudget: Long,
) : BaseEntity() {

    @Column(name = "setting_date", nullable = false, unique = true)
    var settingDate: LocalDate = settingDate
        protected set

    @Column(name = "total_budget", nullable = false)
    var totalBudget: Long = totalBudget
        protected set


    /**
     * 총 예산 업데이트 로직
     */
    fun updateTotalBudget(totalBudget: Long) {
        this.totalBudget = totalBudget
    }
}
